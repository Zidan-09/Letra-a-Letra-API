
export function waitForEvent(name, predicate, events, timeout = 90000) {
    if (!Array.isArray(events)) {
        throw new Error(`waitForEvent [${name}]: events is not an array`);
    }
    if (typeof predicate !== "function") {
        throw new Error(`waitForEvent [${name}]: predicate is not a function`);
    }

    return new Promise((resolve, reject) => {
        const existingIndex = events.findIndex(predicate);

        if (existingIndex !== -1) {
            const [event] = events.splice(existingIndex, 1);
            return resolve(event);
        }

        let timer;

        const handler = (event) => {
            if (!predicate(event)) {
                return false;
            }

            clearTimeout(timer);

            const idx = events.indexOf(event);

            if (idx !== -1) {
                events.splice(idx, 1);
            }

            resolve(event);

            return true;
        };

        events._listeners = events._listeners || [];
        events._listeners.push(handler);

        timer = setTimeout(() => {
            events._listeners = events._listeners.filter(
                listener => listener !== handler
            );

            console.log(`❌ Timeout no evento [${name}]. Eventos acumulados:`);
            console.dir(events, { depth: null });

            reject(new Error(`Timeout esperando ${name}`));
        }, timeout);
    });
}