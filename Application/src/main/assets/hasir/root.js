// LUGAT POPUP WHEN LONG PRESSED
function onContextMenuListener() {
    console.log("JSINT WORKY contextmenu" );
    console.log($(this).text());
    window.androidInterface.receiveSelectedText(window.getSelection().toString(), $(this).text(), langLabel);
}


//GET FIRST PARAGRAF-1 ON CURRENT SCROLL
function getElementByTopOffset(selector, offsetTop){
    return $(selector).filter(function( index, elem) {
       return $(elem).offset().top >= offsetTop;
    }).first();
}

// get prev pageAralik elt in current scroll
function getPrevElementByTopOffset(selector, offsetTop){
    return $(selector).filter(function( index, elem) {
       return $(elem).offset().top <= offsetTop;
    }).last();
}

$( document ).ready(function() {

    // var nextBt = $("<button>==></button>").attr("class", "navButton").click(function() {
    //     window.androidInterface.doSectionChange(sectionNo + 1);
    // });
    // var prevBt = $("<button><==</button>").attr("class", "navButton").click(function() {
    //     window.androidInterface.doSectionChange(sectionNo - 1);
    // });

    // if(sectionNo < lastSection)
    //     $("#container").append(nextBt);
    // if(sectionNo >= 1)
    //     $("#container").prepend(prevBt);

    // for lugat popup
    $(".Paragraf-1, .Paragraf-2").on("contextmenu", onContextMenuListener);

    
    //DETECT SCROLL STOPPING
    $(window).scroll(function() {

        clearTimeout($.data(this, 'scrollTimer'));
        $.data(this, 'scrollTimer', setTimeout(function() {

            $("p").removeClass("highlighted");
            
            console.log("Haven't scrolled in 250ms!");



            var currentPrg = getElementByTopOffset(".Paragraf-1", $(window).scrollTop());
            var currentPage = getPrevElementByTopOffset(".pageNumber", $(window).scrollTop());
            
            currentPrg.addClass("highlighted");
            currentPrg.nextUntil(currentPrg.nextAll(".Paragraf-1").first(), ".Paragraf-2").addClass("highlighted");

            var activeParagraphDistanceToWindow = currentPrg.offset().top - $(window).scrollTop();

            window.androidInterface.scrollFinished(currentPrg.attr("name"), sideLabel, activeParagraphDistanceToWindow, currentPage.attr("name"));

        }, 250));
    });

});

