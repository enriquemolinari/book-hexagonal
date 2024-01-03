package hexagon;

class Rating {

	private int totalUserVotes = 0;
	private float rateValue = 0;
	private float totalValue = 0;

	public Rating(int totalUserVotes, float rateValue, float totalValue) {
		this.totalUserVotes = totalUserVotes;
		this.rateValue = rateValue;
		this.totalValue = totalValue;
	}

	private Rating() {

	}

	public static Rating notRatedYet() {
		return new Rating();
	}

	public void calculaNewRate(int newUserRate) {
		this.rateValue = Math
				.round(((this.totalValue + newUserRate) / (totalUserVotes + 1))
						* 100.0f)
				/ 100.0f;
		this.totalValue += newUserRate;
		this.totalUserVotes++;
	}

	String actualRateAsString() {
		return String.valueOf(this.rateValue);
	}

	float actualRate() {
		return this.rateValue;
	}

	boolean hasValue(float aValue) {
		return this.rateValue == aValue;
	}

	public boolean hastTotalVotesOf(int votes) {
		return this.totalUserVotes == votes;
	}

	int totalVotes() {
		return this.totalUserVotes;
	}

	float totalRateValue() {
		return this.totalValue;
	}
}
