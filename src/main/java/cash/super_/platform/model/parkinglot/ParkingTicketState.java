package cash.super_.platform.model.parkinglot;

public enum ParkingTicketState {

    // ############## User has just arrived at the parking lot

    /**
     * When the user picks up a ticket at the gate, it's time time of when the ticket was created.
     */
    PICKED_UP,
    /**
     * When the user scans the ticket with the application. This is the time of the first ticket status query.
     * The ticket is automatically transitioned into the states in the parking lot.
     */
    SCANNED,

    // ################ User is in the parking lot and is dealing with the ticket

    /**
     * When user gets into the parking lot and the ticket is free
     */
    FREE,
    /**
     * When user gets into the parking lot and the ticket is not free, but is under the grace period
     */
    GRACE_PERIOD,
    /**
     * When the user goes beyond the grace period and hasn't paid the ticket yet.
     * However, if the user pays the ticket and doesn't exit in time, the ticket assumes this state again.
     */
    NOT_PAID,
    /**
     * When the user has paid the ticket and is allowed to exit the parking lot for a given period of time.
     * If the user doesn't leave, the ticket goes back to the not paid again and he/she will be charged.
     */
    PAID,

    // ################## User exits the parking lot

    /**
     * When the user entered and the ticket was for free. When that happens, the the user exited without paying.
     */
    EXITED_ON_FREE,
    /**
     * When the user exited the parking lot when the ticket was in grace period.
     */
    EXITED_ON_GRACE_PERIOD,
    /**
     * When the user exited the parking lot when the ticket was paid at least once.
     */
    EXITED_ON_PAID

}
