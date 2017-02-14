export default class RequestSerializingService {

    static serialize(request) {
        const serializedRequest = {};
        serializedRequest.url = request.url;
        serializedRequest.method = request.method;
        serializedRequest.headers = RequestSerializingService.serializeHeaders(request.headers);
        serializedRequest.referrer = request.referrer;
        serializedRequest.context = request.context;
        serializedRequest.referrerPolicy = request.referrerPolicy;
        serializedRequest.mode = request.mode;
        serializedRequest.credentials = request.credentials;
        serializedRequest.redirect = request.redirect;
        serializedRequest.integrity = request.integrity;
        serializedRequest.cache = request.cache;
        serializedRequest.bodyUsed = false;

        return request.blob().then((blobBody) => {
            serializedRequest.body = blobBody;
            return serializedRequest;
        });
    }

    static serializeHeaders(headers) {
        const serializedHeaders = {};
        for(let key of headers.keys()) {
            serializedHeaders[key] = headers.getAll(key);
        }

        return serializedHeaders;
    }

    static deserialize(serializedRequest) {
        return new Request(serializedRequest.url, serializedRequest);
    }
}