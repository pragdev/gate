package uk.co.pragmaticdevelopers.gate

import javax.servlet.ServletInputStream

class ServletInputStreamMock extends ServletInputStream {
    InputStream inputStream

    ServletInputStreamMock(String body) {
        inputStream = new ByteArrayInputStream(body.bytes)
    }

    @Override
    int read() throws IOException {
        inputStream.read()
    }
}
