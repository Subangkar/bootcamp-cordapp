package bootcamp.flows;

import bootcamp.contracts.CMContract;
import bootcamp.states.CMState;
import bootcamp.DealStatus;
import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AcceptanceFlow {

	@InitiatingFlow
	@StartableByRPC
	public static class Initiator extends FlowLogic<UniqueIdentifier> {

		private UniqueIdentifier proposalId;
		private ProgressTracker progressTracker = new ProgressTracker();

		public Initiator(UniqueIdentifier proposalId) {
			this.proposalId = proposalId;
		}

		@Override
		public ProgressTracker getProgressTracker() {
			return progressTracker;
		}

		@Suspendable
		@Override
		public UniqueIdentifier call() throws FlowException {
			QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(proposalId), Vault.StateStatus.UNCONSUMED, null);
			StateAndRef inputStateAndRef = getServiceHub().getVaultService().queryBy(CMState.class, inputCriteria).getStates().get(0);
			// We choose our transaction's notary (the notary prevents double-spends).
			Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
			CMState input = (CMState) inputStateAndRef.getState().getData();

			/* ============================================================================
			 *         TODO 1 - update CMState!
			 * ===========================================================================*/
			Party client = getOurIdentity();
			//Creating the output
			CMState output = new CMState(input.getClient(), input.getManufacturer(), input.getAuditor(), input.getPayment(), input.getQuantity(), input.getProduct(), DealStatus.FINALIZED.name());
			output.addAuditorAsParticipant();

			CMContract.Commands.Confirm command = new CMContract.Commands.Confirm();
			List<PublicKey> reqSigners = ImmutableList.of(client.getOwningKey(), input.getManufacturer().getOwningKey(), input.getAuditor().getOwningKey());


			/* ============================================================================
			 *      TODO 3 - Build our client proposal transaction to update the ledger!
			 * ===========================================================================*/
			// We build our transaction.
			TransactionBuilder transactionBuilder = new TransactionBuilder();
			transactionBuilder.setNotary(notary);
			transactionBuilder.addInputState(inputStateAndRef);
			transactionBuilder.addOutputState(output, CMContract.ID);
			transactionBuilder.addCommand(command, reqSigners);

			/* ============================================================================
			 *          TODO 2 - Write our CMContract to update proposal!
			 * ===========================================================================*/
			// We check our transaction is valid based on its contracts.
			transactionBuilder.verify(getServiceHub());
			// We sign the transaction with our private key, making it immutable.
			SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

//			FlowSession session = initiateFlow(output.getManufacturer());
//
//			// The counterparty signs the transaction
//			SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));
//
			// We get the transaction notarised and recorded automatically by the platform.
			List<Party> partyList = new ArrayList();
			partyList.add(output.getManufacturer());
			partyList.add(output.getAuditor());
			Set<FlowSession> sessions = partyList.stream().map(it -> initiateFlow(it)).collect(Collectors.toSet());
			final SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction,
					sessions, CollectSignaturesFlow.Companion.tracker()));
			SignedTransaction finalisedTx = subFlow(new FinalityFlow(fullySignedTransaction, sessions));
			return finalisedTx.getTx().outputsOfType(CMState.class).get(0).getLinearId();
		}
	}

	@InitiatedBy(Initiator.class)
	public static class Responder extends FlowLogic<SignedTransaction> {
		private FlowSession otherSide;

		public Responder(FlowSession otherSide) {
			this.otherSide = otherSide;
		}

		@Suspendable
		@Override
		public SignedTransaction call() throws FlowException {
			SignTransactionFlow signTransactionFlow = new SignTransactionFlow(otherSide) {

				@Override
				protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
					try {
						LedgerTransaction ledgerTx = stx.toLedgerTransaction(getServiceHub(), false);
						Party client = ledgerTx.inputsOfType(CMState.class).get(0).getClient();
						if (!client.equals(otherSide.getCounterparty())) {
							throw new FlowException("Only the client can finalize a deal.");
						}
					} catch (SignatureException e) {
						throw new FlowException("Check transaction failed");
					}
				}
			};
			SecureHash txId = subFlow(signTransactionFlow).getId();
			SignedTransaction finalisedTx = subFlow(new ReceiveFinalityFlow(otherSide, txId));
			return finalisedTx;
		}
	}
}


