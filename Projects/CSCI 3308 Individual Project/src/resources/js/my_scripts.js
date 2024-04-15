$("#drinkID").keyup(function(event) {
    if (event.keyCode === 13) { // 13 is the number for hitting enter on keyboard
        $("#buttonID").click();
    }
});

function getDrink(){
	var drink_name = document.getElementById('drinkID').value;
	var url = `https://www.thecocktaildb.com/api/json/v1/1/search.php?s=${drink_name}`;

	console.log(drink_name);

    $.ajax({
		url: url,
		dataType: 'json'
	  })

    .then(function(data){
        console.log(data.drinks[0]);
		var drink = data.drinks[0];

		// 5 variables here
		var name = drink.strDrink;
		var id = drink.idDrink;
		var alc = drink.strAlcoholic;
		var category = drink.strCategory;
		var image = drink.strDrinkThumb;

		// console.log(name);
		// console.log(id);
		// console.log(alc);
		// console.log(category);
		// console.log(image);

		$("#card").empty();

		var card = 
            `<div class="card" style="width: 20rem;">
                <img class="card-img-top" src = "${image}" alt="card image" id="drink_img">
                    <div class="card-body">
                        <h3 class="card-title" style="text-align:center" id="drink_name">${name}</h3>
                        <p class="card-body" id="drink_cat">${category}</p>
                        <p class="card-body" id="drink_alc">${alc}</p>
                        <p class="card-body" id="drink_id">${id}</p>

                    </div>
					<!-- Button trigger modal-->
					<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#modalConfirmDelete">Add to Search History</button>
					
					<!--Modal Here-->
					<div class="modal fade" id="modalConfirmDelete" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
						aria-hidden="true">
						<div class="modal-dialog modal-sm modal-notify modal-danger" role="document">
		
							<div class="modal-content text-center">
								<div class="modal-header d-flex justify-content-center">
									<p class="heading">Are you sure?</p>
								</div>
		
								<div class="modal-body flex-center">
										<a href="" class="btn  btn-outline-danger" data-dismiss="modal">No</a>
										<button type="button" class="btn btn-success" onclick="addDrinkHistory()">Yes</button>
								</div>
							</div>
						</div>
					</div>
            </div>`;

		$("#card").append(card);
    })
}


function addDrinkHistory(){	

	$('#modalConfirmDelete').modal('hide');

	//get variables
	var img = document.getElementById("drink_img").src;
	var name = document.getElementById("drink_name").innerHTML;
	var category = document.getElementById("drink_cat").innerHTML;
	var alc = document.getElementById("drink_alc").innerHTML;
	var id = document.getElementById("drink_id").innerHTML;
	
	// store in a json
	var temp = {img: img, name: name, category: category, alc: alc, id:id}

	console.log(temp);


	$.ajax({
		url: "/search_history", 
		method: "POST",
		dataType: "json",
		contentType: "application/json; charset=utf-8",
		data: JSON.stringify(temp)
	  })

    .then(function(data){
		console.log('added to drinks_db');
	})

}	
