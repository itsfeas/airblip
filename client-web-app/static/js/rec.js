function record() {
    console.log('start recording')
    document.getElementById("message").innerText = "Currently recording...";
    fetch('/start', {
        method: "POST",
        credentials: "include",
        body: JSON.stringify("record"),
        cache: "no-cache",
        headers: new Headers({
            "content-type": "application/json"
        })
    })
    // .then((response) => {
    //     // document.getElementById("message").blur()
    //     // return response.json();
    // })
}