package bootcamp;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

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

	public CMState(Party client, Party manufacturer, Party auditor, int payment, int quantity, String product, String dealStatus) {
		this.client = client;
		this.manufacturer = manufacturer;
		this.auditor = auditor;
		this.payment = payment;
		this.quantity = quantity;
		this.product = product;
		this.dealStatus = dealStatus;
		this.linearId = new UniqueIdentifier();
	}

	@ConstructorForDeserialization
	public CMState(Party client, Party manufacturer, Party auditor, int payment, int quantity, String product, UniqueIdentifier linearId) {
		this.client = client;
		this.manufacturer = manufacturer;
		this.auditor = auditor;
		this.payment = payment;
		this.quantity = quantity;
		this.product = product;
		this.linearId = linearId;
	}

	@NotNull
	@Override
	public UniqueIdentifier getLinearId() {
		return linearId;
	}

	@NotNull
	@Override
	public List<AbstractParty> getParticipants() {
		return ImmutableList.of(client, manufacturer);
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
}
