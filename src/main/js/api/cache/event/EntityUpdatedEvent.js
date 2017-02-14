import Event from './Event';

export default class EntityUpdatedEvent extends Event {

    constructor(eventType, updatedEntity) {
        super(eventType);
        this.updatedEntity = updatedEntity;
    }
}