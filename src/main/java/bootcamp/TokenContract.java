package bootcamp;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

import java.security.PublicKey;
import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
	public static String ID = "bootcamp.TokenContract";


	public void verify(LedgerTransaction tx) throws IllegalArgumentException {
		if (tx.getInputStates().size() != 0)
			throw new IllegalArgumentException();
		if (tx.getOutputStates().size() != 1)
			throw new IllegalArgumentException();
		if (tx.getCommands().size() != 1)
			throw new IllegalArgumentException();

		ContractState output = tx.getOutput(0);
		Command command = tx.getCommand(0);

		if (!(output instanceof TokenState))
			throw new IllegalArgumentException();
		if (!(command.getValue() instanceof Commands.Issue))
			throw new IllegalArgumentException();

		TokenState token = (TokenState) output;
		if (token.getAmount() <= 0)
			throw new IllegalArgumentException();

		List<PublicKey> reqSigners = command.getSigners();
		Party issuer = token.getIssuer();
		PublicKey issuerKey = issuer.getOwningKey();

		if (!(reqSigners.contains(issuerKey)))
			throw new IllegalArgumentException();
	}


	public interface Commands extends CommandData {
		class Issue implements Commands {
		}
	}
}
