package uk.co.pragmaticdevelopers.gate

abstract class Token {
    String value
    Date issuedOn
    Long expiresIn

    Token() {
        this.value = UUID.randomUUID().toString()
        this.issuedOn = new Date()
        this.expiresIn = expiringTime()
    }

    boolean isExpired() {
        def now = new Date()
        now.after expiryDate()
    }

    private Date expiryDate() {
        new Date(issuedOn.time + expiresIn * 1000)
    }

    String toString() {
        value
    }

    abstract def describe()
    int expiringTime() {
        3600
    }
}
