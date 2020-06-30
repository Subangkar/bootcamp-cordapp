package bootcamp.contracts;

import bootcamp.states.OrderState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class OrderContract implements Contract {
	@Override
	public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
		Command command = tx.getCommand(0);
		if (command.getValue() instanceof Commands.Deliver) {
			requireThat(require -> {
				require.using("There should be no inputs", tx.getInputs().size() == 1);
				require.using("Only one output state should be created.", tx.getOutputs().size() == 1);
				require.using("The single output is of type OrderState", tx.outputsOfType(OrderState.class).size() == 1);
				require.using("There is exactly one command", tx.getCommands().size() == 1);
//				require.using("There is no timestamp", tx.getTimeWindow() == null);
				OrderState output = tx.outputsOfType(OrderState.class).get(0);
				require.using("The client is a required signer", command.getSigners().contains(output.getClient().getOwningKey()));
				require.using("quantity can't be -ve", output.getQuantity() > 0);
				require.using("deal status must be `Proposal`", output.getStatus().equals("Delivered"));
				return null;
			});

		} else if (command.getValue() instanceof Commands.Complete) {
			requireThat(require -> {
				require.using("There should be 1 input", tx.getInputs().size() == 1);
				require.using("Only one output state should be created.", tx.getOutputs().size() == 1);
				require.using("The single input is of type CMState", tx.inputsOfType(OrderState.class).size() == 1);
				require.using("The single output is of type CMState", tx.outputsOfType(OrderState.class).size() == 1);
				require.using("There is exactly one command", tx.getCommands().size() == 1);
				OrderState input = tx.inputsOfType(OrderState.class).get(0);
				OrderState output = tx.outputsOfType(OrderState.class).get(0);
//				require.using("Input must be from a Negotiate state", input.getDealStatus().equals("Negotiate"));
				require.using("Output must be from a Finalized state", input.getStatus().equals("Delivered"));
				require.using("Output must be from a Finalized state", output.getStatus().equals("Completed"));
				require.using("The client is unmodified in the output", input.getClient().equals(output.getClient()));
				require.using("The manufacturer is unmodified in the output", input.getManufacturer().equals(output.getManufacturer()));
				require.using("The client is a required signer", command.getSigners().contains(output.getClient().getOwningKey()));
//				require.using("The manufacturer is a required signer", command.getSigners().contains(output.getManufacturer().getOwningKey()));
				return null;
			});
		}
	}

	public interface Commands extends CommandData {
		class Transfer implements CMContract.Commands {
		}

		class Deliver implements CMContract.Commands {
		}

		class Complete implements CMContract.Commands {
		}
	}
}
