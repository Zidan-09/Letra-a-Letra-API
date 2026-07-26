
export function waitForEvent(name, predicate, events, timeout = 5000) {
    return new Promise((resolve, reject) => {
        const existingIndex = events.findIndex(predicate);
        if (existingIndex !== -1) {
            const [event] = events.splice(existingIndex, 1);
            return resolve(event);
        }

        let timer;

        const handler = (event) => {
            if (predicate(event)) {
                clearTimeout(timer);
                const idx = events.indexOf(event);
                if (idx !== -1) events.splice(idx, 1);
                
                resolve(event);
                return true;
            }
            return false;
        };

        events._listeners = events._listeners || [];
        events._listeners.push(handler);

        timer = setTimeout(() => {
            events._listeners = events._listeners.filter(l => l !== handler);
            console.log(`❌ Timeout no evento [${name}]. Eventos acumulados:`);
            console.dir(events, { depth: null });
            reject(new Error(`Timeout esperando ${name}`));
        }, timeout);
    });
}