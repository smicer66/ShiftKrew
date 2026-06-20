package com.syncstate.go.cardinal.inside.ShiftKrew.enums;

public enum PaymentChannel {
	BANK("BANK"), POS("POS"), ONLINE_BANKING("ONLINE BANKING"), MOBILE_MONEY("MOBILE MONEY");


	public final String value;

	PaymentChannel(String value) {
		this.value = value;
	}

	public static PaymentChannel valueOfLabel(String label) {
		for (PaymentChannel e : values()) {
			if (e.value.equals(label)) {
				return e;
			}
		}
		return null;
	}

	public String getValue()
	{
		return this.value;
	}

	public static PaymentChannel getPaymentChannel(String paymentChannel)
	{
		return PaymentChannel.valueOf(paymentChannel);
	}
}
