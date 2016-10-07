/**
 * Created by mariusz on 07.10.16.
 */

import {inject, customAttribute} from "aurelia-framework";

@customAttribute('datepicker')
@inject(Element)
export class DatePicker {
    element:any;

    constructor(element) {
        this.element = element;
    }

    attached() {
        $(this.element).datepicker({ dateFormat: 'yy-mm-dd'})
            .on('change', e => this.fireEvent(e.target, 'input'));

    }

    detached() {
        $(this.element).datepicker('destroy')
            .off('change');
    }

    private createEvent(name) {
        let event = document.createEvent('Event');
        event.initEvent(name, true, true);
        return event;
    }

    private fireEvent(element, name) {
        let event = this.createEvent(name);
        element.dispatchEvent(event);
    }
}

