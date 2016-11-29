import cucumber.api.DataTable
import cucumber.api.PendingException
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks
import goauth.AccessToken
import goauth.Credentials
import groovy.json.JsonSlurper

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Given(~/^valid Resource Owner credentials:$/) { DataTable table ->
  def credentials = table.asMaps(String, String).first()
  username = credentials.username
  password = credentials.password

  store new Credentials(username, password)
}

When(~/^the client makes a POST request to the Authorization Server at the path "([^"]*)"$/) { String path ->
  request = [method: 'post', path: path]
}

When(~/^the body "([^"]*)" contains the parameters:$/) { String contentType, DataTable params ->
  def body = params.asMaps(String, String).first()
  response = restClient."$request.method"(path: request.path, body: body, requestContentType: contentType)
}

And(~/^the request uses the basic authentication scheme$/) { ->
  restClient.auth.basic username, password

  def encodedCredentials = "$username:$password".bytes.encodeBase64().toString()
  restClient.setHeaders([Authorization: "Basic $encodedCredentials"])
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

Given(~/^valid Client credentials:$/) { table ->
  def credentials = table.asMaps(String, String).first()
  username = credentials.username
  password = credentials.password

  store new Credentials(username, password)
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