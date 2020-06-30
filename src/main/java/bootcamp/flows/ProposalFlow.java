package bootcamp.flows;

import bootcamp.contracts.CMContract;
import bootcamp.states.CMState;
import bootcamp.DealStatus;
import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.List;

import static java.util.Collections.singletonList;

public class ProposalFlow {
	@InitiatingFlow
	@StartableByRPC
	public static class Initiator extends FlowLogic<UniqueIdentifier> {

		//		private Party client;
		private Party manufacturer;
		private Party auditor;
		private int payment;
		private int quantity;
		private String product;
		private String dealStatus;

		public Initiator(Party manufacturer, Party auditor, int payment, int quantity, String product) {
			this.manufacturer = manufacturer;
			this.auditor = auditor;
			this.payment = payment;
			this.quantity = quantity;
			this.product = product;
		}

		private final ProgressTracker progressTracker = new ProgressTracker();

		@Override
		public ProgressTracker getProgressTracker() {
			return progressTracker;
		}

		@Suspendable
		@Override
		public UniqueIdentifier call() throws FlowException {
			// We choose our transaction's notary (the notary prevents double-spends).
			Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
			// We get a reference to our own identity.
			Party client = getOurIdentity();

			/* ============================================================================
			 *         TODO 1 - Create CMState to represent on-ledger cm!
			 * ===========================================================================*/
			// We create our new TokenState.
			CMState output = new CMState(client, manufacturer, auditor, payment, quantity, product, DealStatus.PROPOSAL.name());
			CMContract.Commands.Propose command = new CMContract.Commands.Propose();
			List<PublicKey> reqSigners = ImmutableList.of(client.getOwningKey(), manufacturer.getOwningKey());


			/* ============================================================================
			 *      TODO 3 - Build our client proposal transaction to update the ledger!
			 * ===========================================================================*/
			// We build our transaction.
			TransactionBuilder transactionBuilder = new TransactionBuilder();
			transactionBuilder.setNotary(notary);
			transactionBuilder.addOutputState(output, CMContract.ID);
			transactionBuilder.addCommand(command, reqSigners);

			/* ============================================================================
			 *          TODO 2 - Write our CMContract to control proposal!
			 * ===========================================================================*/
			// We check our transaction is valid based on its contracts.
			transactionBuilder.verify(getServiceHub());

			FlowSession session = initiateFlow(manufacturer);

			// We sign the transaction with our private key, making it immutable.
			SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

			// The counterparty signs the transaction
			SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));

			// We get the transaction notarised and recorded automatically by the platform.
			SignedTransaction finalisedTx = subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));
			return finalisedTx.getTx().outputsOfType(CMState.class).get(0).getLinearId();
		}
	}

	@InitiatedBy(Initiator.class)
	public static class Responder extends FlowLogic<Void> {
		private final FlowSession otherSide;

		public Responder(FlowSession otherSide) {
			this.otherSide = otherSide;
		}

		@Override
		@Suspendable
		public Void call() throws FlowException {
			SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(otherSide) {
				@Suspendable
				@Override
				protected void checkTransaction(SignedTransaction stx) throws FlowException {
					// Implement responder flow transaction checks here

				}
			});
			subFlow(new ReceiveFinalityFlow(otherSide, signedTransaction.getId()));
			return null;
		}
	}
}
