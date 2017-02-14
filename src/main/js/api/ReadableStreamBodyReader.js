export default class ReadableStreamBodyReader {

    read(readableStreamBody) {
        const textDecoder = new TextDecoder();
        const reader = readableStreamBody.getReader();
        let bodyAsText = '';

        return reader.read().then(function processReadResult (result) {
            if(result.done) {
                return bodyAsText;
            }

            bodyAsText += textDecoder.decode(result.value, {stream: true});

            return reader.read().then(processReadResult);
        });
    }
}
