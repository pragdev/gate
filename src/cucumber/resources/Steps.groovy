import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks
import uk.co.pragmaticdevelopers.gate.AccessToken
import uk.co.pragmaticdevelopers.gate.AuthorizationCode
import uk.co.pragmaticdevelopers.gate.Client
import uk.co.pragmaticdevelopers.gate.Credentials
import uk.co.pragmaticdevelopers.gate.ResourceOwner
import groovy.json.JsonSlurper

import static uk.co.pragmaticdevelopers.gate.Client.Type.CONFIDENTIAL
import static groovyx.net.http.ContentType.URLENC

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Given(~/^a valid Resource Owner:$/) { DataTable table ->
    def resourceOwnerData = table.asMaps(String, String).first()

    credentials = new Credentials(username: resourceOwnerData.username, password: resourceOwnerData.password)
    resourceOwner = new ResourceOwner(displayName: resourceOwnerData.displayName, username: resourceOwnerData.username, password: resourceOwnerData.password)
    store resourceOwner
}

When(~/^the client makes a POST request to the Authorization Server at the path "([^"]*)"$/) { String path ->
    request = [method: 'post', path: path]
}

When(~/^the client redirects the resource owner to the Authorization Server at the path "([^"]*)"$/) { String path ->
    restClient.setHeaders([Authorization: "Basic ${credentials.encode()}"])
    request = [method: 'get', path: path]
}

When(~/^the body "([^"]*)" contains the parameters:$/) { String contentType, DataTable table ->
    def params = table.asMaps(String, String).first()
    response = restClient."$request.method"(path: request.path, body: params, requestContentType: contentType)
}

When(~/^the query string contains the parameters:$/) { DataTable table ->
    def params = table.asMaps(String, String).first()
    response = restClient."$request.method"(path: request.path, query: params, requestContentType: URLENC)
}

And(~/^the (?:request|resource owner) uses the basic authentication scheme$/) { ->
    restClient.setHeaders([Authorization: "Basic ${credentials.encode()}"])
}

Then(~/^the Authentication Server should respond OK$/) { ->
    assert response.status == 200
}

Then(~/^should be non cacheable$/) { ->
    assert response.headers['Cache-Control'].value == 'no-store'
}

Then(~/^the content type should be JSON$/) { ->
    assert response.contentType == 'application/json'
}

Then(~/^the body should be:$/) { expectedBody ->
    def slurper = new JsonSlurper()
    expectedBody = slurper.parseText(expectedBody)
    def responseBody = response.data

    assert responseBody.size() == 3
    assert responseBody.access_token != null
    assert !responseBody.access_token.empty
    assert responseBody.expires_in == expectedBody.expires_in
    assert responseBody.token_type == expectedBody.token_type
}

Given(~/^a valid Client:$/) { table ->
    def clientData = table.asMaps(String, String).first()
    def id = clientData.id
    def secret = clientData.secret
    redirectionUri = clientData.redirectionUri

    credentials = new Credentials(id, secret)
    client = new Client(id: id, secret: secret, name: "my display name", redirectionUri: new URI(redirectionUri), type: CONFIDENTIAL)
    store client
}

Given(~/^a authentication server administrator has already obtained an access token$/) { ->
    store new AccessToken()
}

When(~/^the admin makes a "([^"]*)" request to the Authorization Server at the path "([^"]*)" with body$/) { String method, String path, String body ->
    response = restClient."${method.toLowerCase()}"(path: path, body: body, requestContentType: 'application/json')
}

Then(~/^the authorization server response should be$/) { expectedBody ->
    def slurper = new JsonSlurper()
    expectedBody = slurper.parseText(expectedBody)
    def responseBody = response.data

    assert responseBody.id.matches(/[A-z0-9-]+/)
    assert responseBody.secret.matches(/[A-z0-9-]+/)

}
And(~/^and the authorization server authenticates the resource owner$/) { ->
    // Write code here that turns the phrase above into concrete actions
//    throw new PendingException()
}
When(~/^the resource owner grants access to the client$/) { ->
    String accessRequestId = response.data.id
    response = restClient.put(path: "/accessrequests", body: [id: accessRequestId, status: 'GRANTED'], requestContentType: 'application/json')
}
Then(~/^the resource owner is redirected to the client$/) { ->
    assert response.status == 302
}
And(~/^the client receives the access token as a uri fragment$/) { ->
    assert response.headers['Location'].value ==~ /$redirectionUri#access_token=([0-9A-z-]+)&token_type=example&expires_in=(\d+)/
}
And(~/^the client receives the authorization token as a uri fragment$/) { ->
    assert response.headers['Location'].value ==~ /$redirectionUri#code=([0-9A-z-]+)&token_type=example&expires_in=(\d+)/
}
And(~/^the client has obtained an Authorization Code already$/) { ->

}
And(~/^the client has obtained an Authorization Code already with value "([^"]*)" and redirect uri "([^"]*)"$/) { String value, String redirectionUri ->
    def code = new AuthorizationCode()
    code.value = value
    store code
}
When(~/^the client requests an access token at the path "([^"]*)" including:$/) { String path, DataTable table ->
    def data = table.asMaps(String, String).first()
    restClient.setHeaders([Authorization: "Basic ${client.credentials.encode()}"])
    response = restClient.post path: path, body: [grant_type: data.grant_type, code: data.code, redirect_uri: data.redirect_uri], requestContentType: URLENC
}
Then(~/^the authorization server authenticates the client$/) { ->
    assert response.status == 200
}
And(~/^verifies the authorization code and the redirection URI matches the one used to obtain it$/) { ->

}
And(~/^the authorization server responds with an access token$/) { ->
    def responseBody = response.data
    assert responseBody.access_token ==~ /[A-z0-9-]+/
    assert responseBody.token_type ==~ 'bearer'
    assert responseBody.expires_in == 3600
}

