package uk.co.pragmaticdevelopers.gate.extension

import uk.co.pragmaticdevelopers.gate.presenter.Presenter

import javax.servlet.http.HttpServletResponse

@Category(HttpServletResponse)
class HttpServletResponseExtension {

    void putAt(String header, Object value) {
        setHeader header, value
    }

    void asJson() {
        contentType = 'application/json'
    }

    void noCache() {
        this['Cache-Control'] = 'no-store'
        this['Pragma'] = 'no-cache'
    }

    void sendJson(content, Presenter presenter) {
        asJson()
        writer << presenter.present(content)
    }

}