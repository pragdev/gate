package goauth.implicitgrant

import goauth.GrantRequestFactory
import goauth.InvalidResponseTypeException
import goauth.MissingQueryParamException
import goauth.Required

import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Field

import static goauth.implicitgrant.AnnotationUtil.fieldName
import static goauth.implicitgrant.AnnotationUtil.hasAnnotation

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
                    .findAll { hasAnnotation(Required, it) }
                    .any { !grantRequest[fieldName(it)] }

            if (isMissingParams) throw new MissingQueryParamException()

            return grantRequest
        } catch (InvalidResponseTypeException ex) {
            throw new MissingQueryParamException()
        }
    }

}
