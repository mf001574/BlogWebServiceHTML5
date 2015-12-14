/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var myForm, fileSelector, progress;
//var url = "/BlogWS2015/resources/files/upload";
var url = "/BlogWS2015/resources/article";
window.onload = function () {
    myForm = document.querySelector("#myForm");
    fileSelector = document.querySelector("#fileSelector");
    progress = document.querySelector("#progress");

}
function sendForm() {
    console.log("in sendForm()");

    // SEND THE FORM USING AJAX

    var myForm = document.querySelector("#myForm");
    var fileSelector = document.querySelector("#fileSelector");

    // On remplit un objet FormData pour envoyer le formulaire
    // (y compris les fichiers attachés) en multipart
    var data = new FormData(myForm);

    var files = fileSelector.files;
    for (var i = 0; i < files.length; i++) {
        var name = "file";
        data.append(name, fileSelector.files[i]);
    }

    sendFormDataWithXhr2(url, data);

    return false;
}

function sendFormDataWithXhr2(url, data) {
    // ajax request
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url); // With FormData,
    // POST is mandatory

    xhr.onload = function () {
        console.log('Upload complete !');

    };

    xhr.onerror = function () {
        console.log("erreur lors de l'envoi");
    }
    
    xhr.upload.onprogress = function (e) {
        progress.value = e.loaded; // number of bytes uploaded
        progress.max = e.total;    // total number of bytes in the file
    };

    // send the request
    xhr.send(data);
}