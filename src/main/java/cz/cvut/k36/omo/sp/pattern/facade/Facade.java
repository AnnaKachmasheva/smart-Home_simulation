package cz.cvut.k36.omo.sp.pattern.facade;

import cz.cvut.k36.omo.sp.model.inhabitant.Inhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.Pet;
import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.event.Event;
import cz.cvut.k36.omo.sp.model.event.TypeEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface Facade {

    /**
     * The device can only be turned on by switching from the OFF state.
     * The device cannot be turned on by a BABY or a person who is not able to state IDLE.
     * The device turns on instantly, so the end time of the event coincides with the start time.
     *
     * @param device     Device to turn on.
     * @param inhabitant Inhabitant who turn off the device.
     * @param dateTime   time and date when the device was turned on.
     */
    void onDevice(Device device, Inhabitant inhabitant, LocalDateTime dateTime);

    /**
     * The device can only be start used when its state is IDLE.
     * If the device is in the state ACTIVE, then the inhabitant is queued to use it. Event WAIT.
     * It will remain in the queue until it waits until the device is used or forced to clear all queues.
     * The device can only be used with inhabitant state IDLE.
     * The end time of the start use event is null.
     * Checking if there is enough content on the device using the method contentReduction.
     * If there is not enough content, an attempt will be made to replenish the content.
     *
     * @param device     device to be used.
     * @param inhabitant the inhabitant who will use this device.
     * @param dateTime   time and date when the device was used.
     */
    void startUseDevice(Device device, Inhabitant inhabitant, LocalDateTime dateTime);

    /**
     * The state of the inhabitant as well as the state of the device must be ACTIVE.
     * If a device has a queue, then the next one in the queue starts using this device.
     *
     * @param inhabitant the Inhabitant who will stop using the device they are currently using.
     * @param dateTime   end time and date of device use event.
     */
    void stopUseDevice(Inhabitant inhabitant, LocalDateTime dateTime);

    /**
     * If the device was used by the inhabitant, then the inhabitant enters the state IDLE.
     * The device usage queue is cleared. All those waiting go into the state IDLE.
     *
     * @param device   Device to stop being used
     * @param dateTime end time and date of device use event.
     */
    void stopUseDevice(Device device, LocalDateTime dateTime);

    /**
     * The device can only be turned off by a person not a BABY.
     *
     * @param device     Device to be turned on.
     * @param inhabitant the Inhabitant to turn off the device.
     * @param dateTime   device turn off time.
     */
    void offDevice(Device device, Inhabitant inhabitant, LocalDateTime dateTime);

    /**
     * The device must be shut down before being fixed.
     * The device manual must be loaded. If it does not find it, the device will be blocked.
     * Fixes happen instantly. Functionality is restored to its original value.
     *
     * @param device   Device to be fixed.
     * @param person   Person who will fix the device.
     * @param dateTime time and date of the fix.
     */
    void fixDevice(Device device, Person person, LocalDateTime dateTime);

    /**
     * The device locks itself.
     * Before blocking, its use is terminated, the pending queue is cleared.
     * Blocking happens instantly.
     *
     * @param device   Device to be locked.
     * @param dateTime time and date of the block.
     */
    void blockDevice(Device device, LocalDateTime dateTime);

    /**
     * @param device   device used.
     * @param dateTime date and time of the end of the previous interval and the beginning of the new one.
     * @param power    device power.
     */
    void addEventEnergy(Device device, LocalDateTime dateTime, int power);

    /**
     * If a person is in a waiting state, then his last event is not closed.
     * If another inhabitant participated in the last event, then his event will be completed.
     * If the last event was using a transport or device, then the next person in the queue will use this one.
     *
     * @param person      the person who completes his last event
     * @param currentTime time and date of the end last event.
     */
    void theEndLastEvent(Person person, LocalDateTime currentTime);

    /**
     * If a pet is in a waiting state, then his last event is not closed.
     * if a pet has used the device, then the next pet in the queue will start using the device.
     * If a person participated in the last event, then the event will be completed and he
     *
     * @param pet         the pet who completes his last event
     * @param currentTime time and date of the end last event.
     */
    void theEndLastEvent(Pet pet, LocalDateTime currentTime);

    /**
     * Used in events related to sensors and devices.
     *
     * @param events   list of events to check.
     * @param dateTime current time and date.
     * @param type     TypeEvent
     * @return true if the event can be combined with the previous one.
     */
    boolean checkLastEvent(List<Event> events, LocalDateTime dateTime, TypeEvent type);
}