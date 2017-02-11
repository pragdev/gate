package uk.co.pragmaticdevelopers.gate

class MissingQueryParamException extends Exception {
    MissingQueryParamException(String message) {
        super(message)
    }

    MissingQueryParamException() {
        super()
    }
}
