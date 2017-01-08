package uk.co.pragmaticdevelopers.gate.httpconverter

import uk.co.pragmaticdevelopers.gate.AnnotationUtil
import uk.co.pragmaticdevelopers.gate.InvalidResponseTypeException
import uk.co.pragmaticdevelopers.gate.MissingQueryParamException
import uk.co.pragmaticdevelopers.gate.Required
import uk.co.pragmaticdevelopers.gate.factory.GrantRequestFactory

import javax.servlet.http.HttpServletRequest

class GrantConverter {

    GrantRequestFactory grantRequestFactory

    def convert(HttpServletRequest request) {
        try {
            Map params = request.queryParams()

            def grantRequest = grantRequestFactory.make(
                    responseType: params.response_type,
                    clientId: params.client_id,
                    redirectUri: params.redirect_uri,
                    scope: params.scope,
                    state: params.state
            )

            def fields = grantRequest.class.declaredFields + grantRequest.class.superclass.declaredFields
            def isMissingParams = fields
                    .findAll { AnnotationUtil.hasAnnotation(Required, it) }
                    .any { !grantRequest[AnnotationUtil.fieldName(it)] }

            if (isMissingParams) throw new MissingQueryParamException()

            return grantRequest
        } catch (InvalidResponseTypeException ex) {
            throw new MissingQueryParamException()
        }
    }

}
