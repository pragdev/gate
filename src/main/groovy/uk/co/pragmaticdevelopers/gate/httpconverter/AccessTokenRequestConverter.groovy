package uk.co.pragmaticdevelopers.gate.httpconverter

import uk.co.pragmaticdevelopers.gate.AnnotationUtil
import uk.co.pragmaticdevelopers.gate.MissingQueryParamException
import uk.co.pragmaticdevelopers.gate.Required
import uk.co.pragmaticdevelopers.gate.factory.AccessTokenRequestFactory

import javax.servlet.http.HttpServletRequest

import static AnnotationUtil.fieldName

class AccessTokenRequestConverter {

    AccessTokenRequestFactory factory

    def convert(HttpServletRequest request) {
        def parameter = request.getParameter('redirect_uri')
        def uri = parameter ? new URI(parameter) : null

        def grantType = request.getParameter('grant_type')
        if (!grantType) throw new MissingQueryParamException("grant_type is missing")

        def accessTokenRequest = factory.make(
                authorizationCode: request.getParameter('code'),
                grantType: grantType,
                redirectUri: uri
        )

        def isMissingParams = accessTokenRequest.class.declaredFields
                .findAll { AnnotationUtil.hasAnnotation(Required, it) }
                .any { !accessTokenRequest[fieldName(it)] }

        if (isMissingParams) throw new MissingQueryParamException()

        return accessTokenRequest
    }
}
