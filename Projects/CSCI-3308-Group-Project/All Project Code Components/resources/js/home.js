// below will be the javascrip that makes the home page function 
function getBooks(author){
    var key = "AIzaSyD0jkTXkzlSo7NL7ZQ1G_3BHP84AsrJAyc";
    //var genre = 'fiction';
    if(author == ""){
        author = "Shakespeare";
    }
    var fav_author = author;
    console.log(author);
    var max = 15;
    
    $.ajax({
      dataType: 'json',
      url: `https://www.googleapis.com/books/v1/volumes?q=inauthor:${fav_author}&printType=BOOKS&maxResults=${max}&orderBy=relevance`
      //url: `https://www.googleapis.com/books/v1/volumes?q=subject:${genre}+inauthor:${fav_author}`,
    })
    
    .then(function(data) {
        // console.log(typeof(data.items[0].volumeInfo.imageLinks));
        // console.log(data.items[1].volumeInfo.imageLinks);
        // console.log(typeof(data.items[2].volumeInfo.imageLinks)== "undefined");
        // console.log(data.items[3].volumeInfo.imageLinks);
        // console.log(data.items[4].volumeInfo.imageLinks);
    
        for(var i=0;i<15;i++){
    
            // see if there is actually a thumbnail
            if(typeof(data.items[i].volumeInfo.imageLinks) == "undefined"){
                var thumb = "https://islandpress.org/sites/default/files/default_book_cover_2015.jpg";
            }
            else{
                var thumb    = data.items[i].volumeInfo.imageLinks.thumbnail;
            }
            
            //var title    = data.items[i].volumeInfo.title;
            //var author   = data.items[i].volumeInfo.authors[0];    
            var book_link = data.items[i].volumeInfo.infoLink;  
    
            if(i<5){
                var temp = document.getElementById("section1");
                if (i==0){
                    temp.innerHTML+= `<a href="#section3" class="arrow__btn">‹</a>`;
                }
                
                temp.innerHTML += 
                `<div class="item">
                    <img src="${thumb}" onclick="window.open('${book_link}')">
                </div>`;
    
                if (i==4){
                    temp.innerHTML+= `<a href="#section2" class="arrow__btn_r">›</a>`;
                }
            }
            else if(i>=5 && i<10){
                //console.log(i)
    
                var temp = document.getElementById("section2");
                if (i==5){
                    temp.innerHTML+= `<a href="#section1" class="arrow__btn">‹</a>`;
                }
                
                temp.innerHTML += 
                `<div class="item">
                    <img src="${thumb}" onclick="window.open('${book_link}')">
                </div>`;
    
                if (i==9){
                    temp.innerHTML+= `<a href="#section3" class="arrow__btn_r">›</a>`;
                }
            }
            else{
                var temp = document.getElementById("section3");
                if (i==10){
                    temp.innerHTML+= `<a href="#section2" class="arrow__btn">‹</a>`;
                }
                
                temp.innerHTML += 
                `<div class="item">
                    <img src="${thumb}" onclick="window.open('${book_link}')">
                </div>`;
    
                if (i==14){
                    temp.innerHTML+= `<a href="#section1" class="arrow__btn_r">›</a>`;
                }
            }
            
        }
    })
    var b = document.getElementById("author1");
                    b.innerHTML += fav_author;
    }
    
    
    function getBooksGenre(author1){
        if(author1 == ""){
            author1 = "Orson Scott Card";
        }
        var author = author1;
        var max = 15;
        
        $.ajax({
          dataType: 'json',
          url: `https://www.googleapis.com/books/v1/volumes?q=inauthor:${author}&printType=BOOKS&maxResults=${max}&orderBy=relevance`
        })
        
        .then(function(data) {
    
            for(var i=0;i<15;i++){
        
                // see if there is actually a thumbnail
                if(typeof(data.items[i].volumeInfo.imageLinks) == "undefined"){
                    var thumb = "https://islandpress.org/sites/default/files/default_book_cover_2015.jpg";
                }
                else{
                    var thumb    = data.items[i].volumeInfo.imageLinks.thumbnail;
                }
                
                //var title    = data.items[i].volumeInfo.title;
                //var author   = data.items[i].volumeInfo.authors[0];    
                var book_link = data.items[i].volumeInfo.infoLink;  
        
                if(i<5){
                    var temp1 = document.getElementById("section1a");
                    if (i==0){
                        temp1.innerHTML+= `<a href="#section3a" class="arrow__btn">‹</a>`;
                    }
                    
                    temp1.innerHTML += 
                    `<div class="item">
                        <img src="${thumb}" onclick="window.open('${book_link}')">
                    </div>`;
        
                    if (i==4){
                        temp1.innerHTML+= `<a href="#section2a" class="arrow__btn_r">›</a>`;
                    }
                }
                else if(i>=5 && i<10){
                    //console.log(i)
        
                    var temp1 = document.getElementById("section2a");
                    if (i==5){
                        temp1.innerHTML+= `<a href="#section1a" class="arrow__btn">‹</a>`;
                    }
                    
                    temp1.innerHTML += 
                    `<div class="item">
                        <img src="${thumb}" onclick="window.open('${book_link}')">
                    </div>`;
        
                    if (i==9){
                        temp1.innerHTML+= `<a href="#section3a" class="arrow__btn_r">›</a>`;
                    }
                }
                else{
                    var temp1 = document.getElementById("section3a");
                    if (i==10){
                        temp1.innerHTML+= `<a href="#section2a" class="arrow__btn">‹</a>`;
                    }
                    
                    temp1.innerHTML += 
                    `<div class="item">
                        <img src="${thumb}" onclick="window.open('${book_link}')">
                    </div>`;
        
                    if (i==14){
                        temp1.innerHTML+= `<a href="#section1a" class="arrow__btn_r">›</a>`;
                    }
                }
                
            }
        })
        var b = document.getElementById("author2");
                    b.innerHTML += author;
        }

            function getBooksGenreB(genre1){
            if(genre1 == ""){
                genre1 = "Fantasy";
            }
            var genre = genre1;
            var max = 15;
            
            $.ajax({
              dataType: 'json',
              url: `https://www.googleapis.com/books/v1/volumes?q=subject:${genre}&printType=BOOKS&maxResults=${max}&orderBy=relevance`
            })
            
            .then(function(data) {
        
                for(var i=0;i<15;i++){
            
                    // see if there is actually a thumbnail
                    if(typeof(data.items[i].volumeInfo.imageLinks) == "undefined"){
                        var thumb = "https://islandpress.org/sites/default/files/default_book_cover_2015.jpg";
                    }
                    else{
                        var thumb    = data.items[i].volumeInfo.imageLinks.thumbnail;
                    }
                    
                    //var title    = data.items[i].volumeInfo.title;
                    //var author   = data.items[i].volumeInfo.authors[0];    
                    var book_link = data.items[i].volumeInfo.infoLink;  
            
                    if(i<5){
                        var temp1 = document.getElementById("section1b");
                        if (i==0){
                            temp1.innerHTML+= `<a href="#section3b" class="arrow__btn">‹</a>`;
                        }
                        
                        temp1.innerHTML += 
                        `<div class="item">
                            <img src="${thumb}" onclick="window.open('${book_link}')">
                        </div>`;
            
                        if (i==4){
                            temp1.innerHTML+= `<a href="#section2b" class="arrow__btn_r">›</a>`;
                        }
                    }
                    else if(i>=5 && i<10){
                        //console.log(i)
            
                        var temp1 = document.getElementById("section2b");
                        if (i==5){
                            temp1.innerHTML+= `<a href="#section1b" class="arrow__btn">‹</a>`;
                        }
                        
                        temp1.innerHTML += 
                        `<div class="item">
                            <img src="${thumb}" onclick="window.open('${book_link}')">
                        </div>`;
            
                        if (i==9){
                            temp1.innerHTML+= `<a href="#section3b" class="arrow__btn_r">›</a>`;
                        }
                    }
                    else{
                        var temp1 = document.getElementById("section3b");
                        if (i==10){
                            temp1.innerHTML+= `<a href="#section2b" class="arrow__btn">‹</a>`;
                        }
                        
                        temp1.innerHTML += 
                        `<div class="item">
                            <img src="${thumb}" onclick="window.open('${book_link}')">
                        </div>`;
            
                        if (i==14){
                            temp1.innerHTML+= `<a href="#section1b" class="arrow__btn_r">›</a>`;
                        }
                    }
                    
                }
            })
            var b = document.getElementById("genre1");
                    b.innerHTML += genre;
            }

            function getBooksGenreC(genre1){
                if(genre1 == ""){
                    genre1 = "Classic";
                }
                var genre = genre1;
                var max = 15;
                
                $.ajax({
                  dataType: 'json',
                  url: `https://www.googleapis.com/books/v1/volumes?q=subject:${genre}&printType=BOOKS&maxResults=${max}&orderBy=relevance`
                })
                
                .then(function(data) {
            
                    for(var i=0;i<15;i++){
                
                        // see if there is actually a thumbnail
                        if(typeof(data.items[i].volumeInfo.imageLinks) == "undefined"){
                            var thumb = "https://islandpress.org/sites/default/files/default_book_cover_2015.jpg";
                        }
                        else{
                            var thumb    = data.items[i].volumeInfo.imageLinks.thumbnail;
                        }
                        
                        //var title    = data.items[i].volumeInfo.title;
                        //var author   = data.items[i].volumeInfo.authors[0];    
                        var book_link = data.items[i].volumeInfo.infoLink;  
                
                        if(i<5){
                            var temp1 = document.getElementById("section1c");
                            if (i==0){
                                temp1.innerHTML+= `<a href="#section3c" class="arrow__btn">‹</a>`;
                            }
                            
                            temp1.innerHTML += 
                            `<div class="item">
                                <img src="${thumb}" onclick="window.open('${book_link}')">
                            </div>`;
                
                            if (i==4){
                                temp1.innerHTML+= `<a href="#section2c" class="arrow__btn_r">›</a>`;
                            }
                        }
                        else if(i>=5 && i<10){
                            //console.log(i)
                
                            var temp1 = document.getElementById("section2c");
                            if (i==5){
                                temp1.innerHTML+= `<a href="#section1c" class="arrow__btn">‹</a>`;
                            }
                            
                            temp1.innerHTML += 
                            `<div class="item">
                                <img src="${thumb}" onclick="window.open('${book_link}')">
                            </div>`;
                
                            if (i==9){
                                temp1.innerHTML+= `<a href="#section3c" class="arrow__btn_r">›</a>`;
                            }
                        }
                        else{
                            var temp1 = document.getElementById("section3c");
                            if (i==10){
                                temp1.innerHTML+= `<a href="#section2c" class="arrow__btn">‹</a>`;
                            }
                            
                            temp1.innerHTML += 
                            `<div class="item">
                                <img src="${thumb}" onclick="window.open('${book_link}')">
                            </div>`;
                
                            if (i==14){
                                temp1.innerHTML+= `<a href="#section1c" class="arrow__btn_r">›</a>`;
                            }
                        }
                        
                    }
                })
                var b = document.getElementById("genre2");
                    b.innerHTML += genre;
                }

                function getBooksGenreD(genre1){
                    if(genre1 == ""){
                        genre1 = "Horror";
                    }
                    console.log((genre1));
                    
                    var genre = genre1;
                    var max = 15;
                    
                    $.ajax({
                      dataType: 'json',
                      url: `https://www.googleapis.com/books/v1/volumes?q=subject:${genre}&printType=BOOKS&maxResults=${max}&orderBy=relevance`
                    })
                    
                    .then(function(data) {
                
                        for(var i=0;i<15;i++){
                    
                            // see if there is actually a thumbnail
                            if(typeof(data.items[i].volumeInfo.imageLinks) == "undefined"){
                                var thumb = "https://islandpress.org/sites/default/files/default_book_cover_2015.jpg";
                            }
                            else{
                                var thumb    = data.items[i].volumeInfo.imageLinks.thumbnail;
                            }
                            
                            //var title    = data.items[i].volumeInfo.title;
                            //var author   = data.items[i].volumeInfo.authors[0];    
                            var book_link = data.items[i].volumeInfo.infoLink;  
                    
                            if(i<5){
                                var temp1 = document.getElementById("section1d");
                                if (i==0){
                                    temp1.innerHTML+= `<a href="#section3d" class="arrow__btn">‹</a>`;
                                }
                                
                                temp1.innerHTML += 
                                `<div class="item">
                                    <img src="${thumb}" onclick="window.open('${book_link}')">
                                </div>`;
                    
                                if (i==4){
                                    temp1.innerHTML+= `<a href="#section2d" class="arrow__btn_r">›</a>`;
                                }
                            }
                            else if(i>=5 && i<10){
                                //console.log(i)
                    
                                var temp1 = document.getElementById("section2d");
                                if (i==5){
                                    temp1.innerHTML+= `<a href="#section1d" class="arrow__btn">‹</a>`;
                                }
                                
                                temp1.innerHTML += 
                                `<div class="item">
                                    <img src="${thumb}" onclick="window.open('${book_link}')">
                                </div>`;
                    
                                if (i==9){
                                    temp1.innerHTML+= `<a href="#section3d" class="arrow__btn_r">›</a>`;
                                }
                            }
                            else{
                                var temp1 = document.getElementById("section3d");
                                if (i==10){
                                    temp1.innerHTML+= `<a href="#section2d" class="arrow__btn">‹</a>`;
                                }
                                
                                temp1.innerHTML += 
                                `<div class="item">
                                    <img src="${thumb}" onclick="window.open('${book_link}')">
                                </div>`;
                    
                                if (i==14){
                                    temp1.innerHTML+= `<a href="#section1d" class="arrow__btn_r">›</a>`;
                                }
                            }
                            
                            
                        }
                    })
                    var b = document.getElementById("genre3");
                    b.innerHTML += genre;
                    }
    
