package bootcamp.states;

import bootcamp.contracts.CMContract;
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

@BelongsToContract(CMContract.class)
public class CMState implements LinearState {
	private Party client;
	private Party manufacturer;
	private Party auditor;
	private int payment;
	private int quantity;
	private String product;
	private String dealStatus;
	private UniqueIdentifier linearId;
	private int providedPayment;

	private List<AbstractParty> participants;

	public CMState(Party client, Party manufacturer, Party auditor, int payment, int quantity, String product, String dealStatus) {
		this.client = client;
		this.manufacturer = manufacturer;
		this.auditor = auditor;
		this.payment = payment;
		this.quantity = quantity;
		this.product = product;
		this.dealStatus = dealStatus;
		this.linearId = new UniqueIdentifier();

		participants = new ArrayList<AbstractParty>(Arrays.asList(client, manufacturer));
	}

	public CMState(Party client, Party manufacturer, Party auditor, int payment, int quantity, String product, String dealStatus, int providedPayment) {
		this.client = client;
		this.manufacturer = manufacturer;
		this.auditor = auditor;
		this.payment = payment;
		this.quantity = quantity;
		this.product = product;
		this.dealStatus = dealStatus;
		this.linearId = new UniqueIdentifier();
		this.providedPayment = providedPayment;

		participants = new ArrayList<AbstractParty>(Arrays.asList(client, manufacturer));
	}

	@ConstructorForDeserialization
	public CMState(Party client, Party manufacturer, Party auditor, int payment, int quantity, String product, String dealStatus, UniqueIdentifier linearId, int providedPayment, List<AbstractParty> participants) {
		this.client = client;
		this.manufacturer = manufacturer;
		this.auditor = auditor;
		this.payment = payment;
		this.quantity = quantity;
		this.product = product;
		this.dealStatus = dealStatus;
		this.linearId = linearId;
		this.providedPayment = providedPayment;
		this.participants = participants;
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

	public Party getAuditor() {
		return auditor;
	}

	public int getPayment() {
		return payment;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getProduct() {
		return product;
	}

	public String getDealStatus() {
		return dealStatus;
	}

	public void addAuditorAsParticipant() {
		participants.add(auditor);
	}
}
