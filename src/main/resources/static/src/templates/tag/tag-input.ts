import {customElement} from "aurelia-framework";
/**
 * Created by mariusz on 01.11.16.
 */

@customElement('tag-input')
export class TagInput{

    extractTagNames(newTags) {
        const extractedTags = newTags.split(/(?:[,.;:]| )+/).map(tag => tag.substring(tag.lastIndexOf('#') + 1)).filter(text => text.length > 0).join(',');
        window.location.href = `#/tags/${extractedTags}`;
    }
}