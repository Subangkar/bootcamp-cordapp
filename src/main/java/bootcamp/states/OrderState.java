package bootcamp.states;

import bootcamp.contracts.OrderContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@BelongsToContract(OrderContract.class)
public class OrderState implements LinearState {
	private Party client;
	private Party manufacturer;
	private Party supplier;
	private int quantity;
	private String product;
	private String status;
	private Party previousHolder;
	private Party currentHolder;

	private UniqueIdentifier linearId;

	private List<AbstractParty> participants;

	public OrderState(Party client, Party manufacturer, Party supplier, int quantity, String product, String status, Party previousHolder, Party currentHolder) {
		this.client = client;
		this.manufacturer = manufacturer;
		this.supplier = supplier;
		this.quantity = quantity;
		this.product = product;
		this.status = status;
		this.previousHolder = previousHolder;
		this.currentHolder = currentHolder;
		this.participants = new ArrayList<AbstractParty>(Arrays.asList(client, manufacturer));
	}

	@ConstructorForDeserialization
	public OrderState(Party client, Party manufacturer, Party supplier, int quantity, String product, String status, Party previousHolder, Party currentHolder, UniqueIdentifier linearId, List<AbstractParty> participants) {
		this.client = client;
		this.manufacturer = manufacturer;
		this.supplier = supplier;
		this.quantity = quantity;
		this.product = product;
		this.status = status;
		this.previousHolder = previousHolder;
		this.currentHolder = currentHolder;
		this.linearId = linearId;
		this.participants = participants;
	}

	public Party getSupplier() {
		return supplier;
	}

	public String getStatus() {
		return status;
	}

	public Party getPreviousHolder() {
		return previousHolder;
	}

	public Party getCurrentHolder() {
		return currentHolder;
	}

	@NotNull
	@Override
	public UniqueIdentifier getLinearId() {
		return linearId;
	}

	@NotNull
	@Override
	public List<AbstractParty> getParticipants() {
//		return ImmutableList.of(client, manufacturer);
		return participants;
	}

	public Party getClient() {
		return client;
	}

	public Party getManufacturer() {
		return manufacturer;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getProduct() {
		return product;
	}

	public void addAuditorAsParticipant(Party participant, String role) {
		participants.add(participant);
	}
}
