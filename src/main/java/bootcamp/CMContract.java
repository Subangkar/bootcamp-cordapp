package bootcamp;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class CMContract implements Contract {
	public static String ID = "bootcamp.CMContract";

	@Override
	public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
//		final CommandWithParties command = tx.getCommands().get( 0 );
//		if (command.getValue() instanceof Commands.Propose) {
//			requireThat( require -> {
//				require.using( "There should be no inputs" , tx.getInputs().isEmpty() );
//				require.using( "Only one output state should be created." , tx.getOutputs().size() == 1 );
//				require.using( "The single output is of type CMState" , tx.outputsOfType( CMState.class ).size() == 1 );
//				require.using( "There is exactly one command" , tx.getCommands().size() == 1 );
////				require.using("There is no timestamp", tx.getTimeWindow() == null);
//				CMState output = tx.outputsOfType( CMState.class ).get( 0 );
////				require.using("The buyer and seller are the proposer and the proposee", ImmutableSet.of(output.getBuyer(), output.getSeller()).equals(ImmutableSet.of(output.getProposee(), output.getProposer())));
//				require.using( "The client is a required signer" , command.getSigners().contains( output.getClient().getOwningKey() ) );
//				require.using( "payment can't be -ve" , output.getPayment() > 0 );
//				require.using( "deal status must be `Proposal`" , output.getDealStatus().equals( "Proposal" ) );
////				require.using("The proposee is a required signer", command.getSigners().contains(output.getProposee().getOwningKey()));
//				return null;
//			} );
//		} else if (command.getValue() instanceof Commands.Negotiate) {
//			requireThat( require -> {
//				require.using( "There should be 1 input" , tx.getInputs().size() == 1 );
//				require.using( "Only one output state should be created." , tx.getOutputs().size() == 1 );
//				require.using( "The single input is of type CMState" , tx.inputsOfType( CMState.class ).size() == 1 );
//				require.using( "The single output is of type CMState" , tx.outputsOfType( CMState.class ).size() == 1 );
//				require.using( "There is exactly one command" , tx.getCommands().size() == 1 );
//				CMState input = tx.inputsOfType( CMState.class ).get( 0 );
//				CMState output = tx.outputsOfType( CMState.class ).get( 0 );
//				require.using( "Must be from a Proposal state" , input.getDealStatus().equals( "Proposal" ) );
//				require.using( "The client is unmodified in the output" , input.getClient().equals( output.getClient() ) );
//				require.using( "The manufacturer is unmodified in the output" , input.getManufacturer().equals( output.getManufacturer() ) );
//				require.using( "The auditor is unmodified in the output" , input.getAuditor().equals( output.getAuditor() ) );
//				require.using( "The client is a required signer" , command.getSigners().contains( output.getClient().getOwningKey() ) );
//				require.using( "The manufacturer is a required signer" , command.getSigners().contains( output.getManufacturer().getOwningKey() ) );
//				return null;
//			} );
//
//		} else if (command.getValue() instanceof Commands.Confirm) {
//			requireThat( require -> {
//				require.using( "There should be 1 input" , tx.getInputs().size() == 1 );
//				require.using( "Only one output state should be created." , tx.getOutputs().size() == 1 );
//				require.using( "The single input is of type CMState" , tx.inputsOfType( CMState.class ).size() == 1 );
//				require.using( "The single output is of type CMState" , tx.outputsOfType( CMState.class ).size() == 1 );
//				require.using( "There is exactly one command" , tx.getCommands().size() == 1 );
//				CMState input = tx.inputsOfType( CMState.class ).get( 0 );
//				CMState output = tx.outputsOfType( CMState.class ).get( 0 );
//				require.using( "Must be from a Response state" , input.getDealStatus().equals( "Respond" ) );
//				require.using( "The client is unmodified in the output" , input.getClient().equals( output.getClient() ) );
//				require.using( "The manufacturer is unmodified in the output" , input.getManufacturer().equals( output.getManufacturer() ) );
//				require.using( "The auditor is unmodified in the output" , input.getAuditor().equals( output.getAuditor() ) );
//				require.using( "The client is a required signer" , command.getSigners().contains( output.getClient().getOwningKey() ) );
//				require.using( "The manufacturer is a required signer" , command.getSigners().contains( output.getManufacturer().getOwningKey() ) );
//				return null;
//			} );
//
//		}

		return;
	}

	public interface Commands extends CommandData {
		class Propose implements Commands {
		}

		class Negotiate implements Commands {
		}

		class Confirm implements Commands {
		}

		class OrderStart implements Commands {
		}
	}
}
