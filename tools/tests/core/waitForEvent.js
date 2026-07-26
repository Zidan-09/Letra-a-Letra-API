export function waitForEvent(name, predicate, events, timeout = 5000) {
    return new Promise((resolve, reject) => {
        const start = Date.now();

        const interval = setInterval(() => {
            const index = events.findIndex(predicate);

            if (index !== -1) {
                const event = events.splice(index, 1)[0];
                clearInterval(interval);
                resolve(event);
            }

            if (Date.now() - start > timeout) {
                clearInterval(interval);

                console.log("Eventos disponíveis:");
                console.dir(events, { depth: null });

                reject(new Error(`Timeout esperando ${name}`));
            }
        }, 10);
    });
}