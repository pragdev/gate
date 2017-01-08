package uk.co.pragmaticdevelopers.gate.extension

import javax.servlet.http.HttpServletResponse

@Category(HttpServletResponse)
class HttpServletResponseExtension {

    void putAt(String header, Object value) {
        setHeader header, value
    }

}