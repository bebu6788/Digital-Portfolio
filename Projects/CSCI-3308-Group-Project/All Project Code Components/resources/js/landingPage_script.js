function openModal() {
  var myInput = document.getElementById("psw");
  var confirmMyInput = document.getElementById("cpsw");
  var letter = document.getElementById("letter");
  var capital = document.getElementById("capital");
  var number = document.getElementById("number");
  var symbol = document.getElementById("symbol"); 
  var length = document.getElementById("length");
  var match = document.getElementById("match");

  // When the user starts to type something inside the password field
  myInput.onkeyup = function () {
    console.log("helllooo");

    var lowerCaseLetters = /[a-z]/g; 
    var upperCaseLetters = /[A-Z]/g; 
    var numbers = /[0-9]/g; 
    var symbols = /[`!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/g; 
    var minLength = 9; 

    // Validate lowercase letters
    if (myInput.value.match(lowerCaseLetters)) {
      letter.classList.remove("invalid");
      letter.classList.add("valid");
    } else {
      letter.classList.remove("valid");
      letter.classList.add("invalid");
    }

    // Validate capital letters
    if (myInput.value.match(upperCaseLetters)) {
      capital.classList.remove("invalid");
      capital.classList.add("valid");
    } else {
      capital.classList.remove("valid");
      capital.classList.add("invalid");
    }

    // Validate numbers
    if (myInput.value.match(numbers)) {
      number.classList.remove("invalid");
      number.classList.add("valid");
    } else {
      number.classList.remove("valid");
      number.classList.add("invalid");
    }

    // Validate symbols
    if (myInput.value.match(symbols)) {
      symbol.classList.remove("invalid");
      symbol.classList.add("valid");
    } else {
      symbol.classList.remove("valid");
      symbol.classList.add("invalid");
    }

    // Validate length
    if (myInput.value.length >= minLength) {
      length.classList.remove("invalid");
      length.classList.add("valid");
    } else {
      length.classList.remove("valid");
      length.classList.add("invalid");
    }
    
  };;
 
  confirmMyInput.onkeyup = function () {
    // Validate password and confirmPassword
    var passEqualsConfPass = myInput.value === confirmMyInput.value; 
    if (passEqualsConfPass) {
      match.classList.remove("invalid");
      match.classList.add("valid");
    } else {
      match.classList.remove("valid");
      match.classList.add("invalid");
    }

    // Disable or Enable the button based on the elements in classList
    enableButton(letter, capital, number, length, match);
  };
}

function enableButton(letter, capital, number, length, match) {
  var button = document.getElementById("my_submit_button");
  var condition = letter.classList.value === 'valid' && capital.classList.value === 'valid' && number.classList.value === 'valid' && length.classList.value === 'valid' && match.classList.value === 'valid'; // TODO: Replace false with the correct condition
  if (condition) {
    button.disabled = false;
  }
}