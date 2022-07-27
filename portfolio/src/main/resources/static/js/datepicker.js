/**
 * Configures the element with the given id to be a date and time picker. Returns the object of the datetime picker
 * for setting to a variable so changes can be made with code (e.g. setting the date).
 * @param elementId id of element to configure
 * @returns {tempusDominus.TempusDominus} returns the object of the datetime picker
 */
function configureDateTimePicker(elementId) {
    const datepicker = new tempusDominus.TempusDominus(
        document.getElementById(elementId),
        {
            useCurrent: false,
            display: {
                icons: {
                    time: 'bi bi-clock',
                    date: 'bi bi-calendar',
                    up: 'bi bi-arrow-up',
                    down: 'bi bi-arrow-down',
                    previous: 'bi bi-chevron-left',
                    next: 'bi bi-chevron-right',
                    today: 'bi bi-calendar-check',
                },
                buttons: {
                    today: true,
                },
                components: {
                    useTwentyfourHour: false
                },
                theme: 'light'
            },
        }
    );
    datepicker.dates.formatInput = function(date) { { return moment(date).format('DD/MMM/yyyy h:mm a') } }
    return datepicker
}