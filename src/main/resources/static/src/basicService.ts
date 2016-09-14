import {HttpClient} from "aurelia-fetch-client";
/**
 * Created by mariusz on 14.09.16.
 */


export abstract class BasicService {
    protected httpClient:HttpClient;

    constructor(httpClient:HttpClient) {
        this.httpClient = httpClient;
    }
}