document.getElementById("joinBtn").addEventListener("click",()=>{
    clearErrMessage();
    document.querySelector(".notifyDisableContainer").classList.add("hideDiv");
    document.querySelector(".notifyEnableContainer").classList.add("showDiv");
    document.querySelector(".notifyEnableContainer").classList.remove("hideDiv");
});
document.getElementById("unSubBtn").addEventListener("click",()=>{
    clearErrMessage();
    document.querySelector(".notifyEnableContainer").classList.add("hideDiv");
    document.querySelector(".notifyDisableContainer").classList.add("showDiv");
    document.querySelector(".notifyDisableContainer").classList.remove("hideDiv");
});
document.querySelector(".cancelBtnOne").addEventListener("click",()=>{
    clearErrMessage();
    document.querySelector(".notifyEnableContainer").classList.add("hideDiv");
    document.querySelector(".notifyDisableContainer").classList.add("hideDiv");
});
document.querySelector(".cancelBtnTwo").addEventListener("click",()=>{
    clearErrMessage();
    document.querySelector(".notifyEnableContainer").classList.add("hideDiv");
    document.querySelector(".notifyDisableContainer").classList.add("hideDiv");
});

function clearErrMessage(){
    document.querySelectorAll(".errMessage").forEach(el => el.textContent = '');
}

function validateEmailRegex(email){
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
}

function validateNotifyEnableFields(){
    clearErrMessage();
    let errorMsg = document.querySelector(".errMessage");
    let email = document.getElementById("joinUserEmail").value;
    let keywords = document.getElementById("userKeywords").value;
    if(email.trim() === ""){
        errorMsg.textContent = "Email field should not be empty!";
        return false;
    }else if(!validateEmailRegex(email)){
        errorMsg.textContent = "Please provide valid email!";
        return false;
    }else if(email.length > 200){
        errorMsg.textContent = "Email length limit exceeded!";
        return false;
    }
    if(keywords.trim() === ""){
        errorMsg.textContent = "Keywords field should not be empty!";
        return false;
    }else{
        let keywordArr = keywords.split(",");
        for(let keyword of keywordArr){
            if(keyword.length > 100){
                errorMsg.textContent = "Maximum length allowed for each keyword is 100!";
                return false;
            }
        }
    }
    return true;
}

function validateNotifyDisableFields(){
    clearErrMessage();
    let errorMsg = document.querySelector(".errMessage");
    let email = document.getElementById("leaveUserEmail").value;
    if(email.trim() === ""){
        errorMsg.textContent = "Email field should not be empty!";
        return false;
    }else if(!validateEmailRegex(email)){
        errorMsg.textContent = "Please provide valid email!";
        return false;
    }else if(email.length > 200){
        errorMsg.textContent = "Email length limit exceeded!";
        return false;
    }
    return true;
}