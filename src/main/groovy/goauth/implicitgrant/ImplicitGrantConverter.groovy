package goauth.implicitgrant

import goauth.MissingQueryParam
import goauth.Required

import javax.servlet.http.HttpServletRequest

import static goauth.implicitgrant.AnnotationUtil.fieldName
import static goauth.implicitgrant.AnnotationUtil.hasAnnotation

class ImplicitGrantConverter {

    ImplicitGrantRequest convert(HttpServletRequest request) {
        Map params = request.queryParams()

        def grantRequest = new ImplicitGrantRequest(
                redirectUri: params.redirect_uri,
                responseType: params.response_type,
                state: params.state,
                scope: params.scope,
                clientId: params.client_id
        )

        def isMissingParams = grantRequest.class.declaredFields
                .findAll { hasAnnotation(Required, it) }
                .any { !grantRequest[fieldName(it)] }

        if (isMissingParams) throw new MissingQueryParam()

        return grantRequest
    }

}
