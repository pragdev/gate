package goauth

import javax.servlet.http.HttpServletRequest

import static goauth.implicitgrant.AnnotationUtil.fieldName
import static goauth.implicitgrant.AnnotationUtil.hasAnnotation

class AccessTokenRequestConverter {

    AccessTokenRequestFactory factory

    def convert(HttpServletRequest request) {
        def parameter = request.getParameter('redirect_uri')
        def uri = parameter ? new URI(parameter) : null

        def grantType = request.getParameter('grant_type')
        if (!grantType) throw new MissingQueryParamException()

        def accessTokenRequest = factory.make(
                authorizationCode: request.getParameter('code'),
                grantType: grantType,
                redirectUri: uri
        )

        def isMissingParams = accessTokenRequest.class.declaredFields
                .findAll { hasAnnotation(Required, it) }
                .any { !accessTokenRequest[fieldName(it)] }

        if (isMissingParams) throw new MissingQueryParamException()

        return accessTokenRequest
    }
}
