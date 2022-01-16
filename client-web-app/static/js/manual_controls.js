function record() {
    fetch('/record', {
        method: "POST",
        credentials: "include",
        body: JSON.stringify(timeText.textContent),
        cache: "no-cache",
        headers: new Headers({
            "content-type": "application/json"
        })
    })
        .then((response) => {
            return response.json();
        })
}