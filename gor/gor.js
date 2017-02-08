/**
 * Created by sigmar on 30/03/16.
 */

function resize() {
    var ta = document.getElementById("gorscript");
    ta.style.width = (window.innerWidth-20)+"px";
    ta.style.height = "200px";

    var c = document.getElementById("gortable");
    c.style.width = (window.innerWidth-15)+"px";
    c.style.height = (window.innerHeight-290)+"px";
    c.width = (window.innerWidth-15)*2;
    c.height = (window.innerHeight-290)*2;

    var b = document.getElementById("gorrun");
    b.style.width = (window.innerWidth-15)+"px";
    b.style.height = "30px";

    var d = document.getElementById("gorstatus");
    d.style.width = (window.innerWidth-15)+"px";
    d.style.height = "50px";

    draw( c );
}

var yoffset = 0;
var currentgor;
var header;
function draw( c ) {
    console.log("yoffset " + yoffset);
    var ctx = c.getContext("2d");
    ctx.font = "24px Arial";
    ctx.clearRect(0,0,c.width,c.height);
    if( currentgor != null && currentgor.length > 0 ) {
        var rowoffset = 30;

        ctx.fillStyle = "#999999";
        ctx.fillRect(0, 0, c.width, rowoffset + 3);
        ctx.fillStyle = "#111111";

        var off = 0;
        var colsize = c.width/header.length;
        var gor = currentgor;
        for (var h of header) {
            ctx.fillText(h, off + 5, rowoffset - 5);
            off += colsize;
        }
        rowoffset += 30;

        var offi = Math.floor( yoffset/30 );
        start = idx[offi]+1;
        offi++;
        i = idx[offi];

        while (rowoffset < c.height) {
            var line = gor.substr(start, i - start);
            //console.log("line " + line + "  " + offi + "  " + start + "  " + i);
            var linesplit = line.split("\t");
            off = 0;

            if (offi % 2 == 1) {
                ctx.fillStyle = "#EEEEEE";
                ctx.fillRect(0, rowoffset - 28, c.width, 30);
                ctx.fillStyle = "#111111";
            }
            for (var l of linesplit) {
                ctx.fillText(l, off + 5, rowoffset - 5);
                off += colsize;
            }
            rowoffset += 30;
            start = i + 1;

            offi++;
            i = idx[offi];
        }
    }
}

var respUrl;
function nloadListener () {
    console.log('nload ' + this.responseText);
}

var idx;
function nloadendListener () {
    idx = [];
    
    currentgor = this.responseText;
    var gor = currentgor;
    var c = document.getElementById("gortable");
    var start = 0;
    var i = gor.indexOf("\n");
    var headstr = gor.substr(start, i);
    header = headstr.split("\t");

    yoffset = 0;
    var count = 0;
    start = i+1;
    idx[0] = i;
    var i = gor.indexOf("\n", start);
    while( i != -1 ) {
        start = i+1;
        count++;
        idx[count] = i;
        i = gor.indexOf("\n", start);
    }

    var d = document.getElementById("gorstatus");
    d.innerHTML = "Got lines: "+idx.length;
    draw( c );
}

function nerrorListener (error) {
    console.log('nerror ' + error.message);
}

function loadListener () {
    console.log('load ' + this.responseText);
}

function errorListener (error) {
    console.log('error ' + error.message);
}

function loadendListener () {
    console.log('loadend ' + this.responseText);

    var req = new XMLHttpRequest();
    req.addEventListener("load", nloadListener);
    req.addEventListener("loadend", nloadendListener);
    req.addEventListener("error", nerrorListener);
    req.onreadystatechange = function() {
        if (this.readyState === this.DONE) {
            console.log(this.responseURL);
        }
    };
    req.open("GET", respUrl + "next");

    var ta = document.getElementById("gorscript");
    req.send( ta.value );
}

function start() {
    var t = document.createElement("textarea");
    t.id = "gorscript";
    t.style.width = (window.innerWidth-20)+"px";
    t.style.height = "200px";

    var c = document.createElement("canvas");
    c.id = "gortable";
    c.style.width = (window.innerWidth-15)+"px";
    c.style.height = (window.innerHeight-290)+"px";
    c.width = (window.innerWidth-15)*2;
    c.height = (window.innerHeight-290)*2;
    c.onmousewheel = function( e ) {
        e.preventDefault();
        var c = document.getElementById("gortable");
        yoffset = Math.min( idx.length*30-c.height, Math.max(0, yoffset+e.wheelDelta) );
        draw( c );
    }

    var b = document.createElement("button");
    b.id = "gorrun";
    b.onclick = function() {
        var req = new XMLHttpRequest();
        req.addEventListener("load", loadListener);
        req.addEventListener("loadend", loadendListener);
        req.addEventListener("error", errorListener);
        req.onreadystatechange = function() {
            if (this.readyState === this.DONE) {
                respUrl = this.responseURL;
            }
        };
        req.open("POST", "http://52.70.161.146/gorserver/gor/pipes?lines=1000000007&zip=N&cache=N");
        //req.setRequestHeader("Access-Control-Request-Headers", "X-Custom-Header");
        //req.setRequestHeader("Access-Control-Request-Methods", "POST");
        //req.open("GET", "http://localhost:8887/gorserver/gor/status");
        console.log("about to send " + t.value);
        req.send( t.value );
    }
    b.textContent = "Run gor"
    b.style.width = (window.innerWidth-15)+"px";
    b.style.height = "30px";

    var d = document.createElement("div");
    d.id = "gorstatus";
    d.style.width = (window.innerWidth-15)+"px";
    d.style.height = "50px";
    d.innerHTML = "";

    draw( c );

    var cont = document.getElementById("content");
    cont.appendChild(t);
    cont.appendChild(b);
    cont.appendChild(c);
    cont.appendChild(d);
}
