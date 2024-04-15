const axios = require('axios');
var express = require('express'); //Ensure our express framework has been added
var app = express();
var bodyParser = require('body-parser'); //Ensure our body-parser tool has been added
app.use(bodyParser.json());              // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies
const cookieParser = require('cookie-parser');
app.use(cookieParser());


//Create Database Connection
var pgp = require('pg-promise')();

const dbConfig = {
	host: 'db',
	port: 5432,
	database: 'user_db',
	user: 'postgres',
	password: 'pwd'
};

var db = pgp(dbConfig);

// set the view engine to ejs
app.set('view engine', 'ejs');
app.use(express.static(__dirname + '/'));

app.get('/', function(req, res) {
	res.render('pages/login',{
		local_css:"signin.css",
		my_title:"Welcome"
	});
});

// REGISTER
app.post('/', function(req, res) {
	var firstNameQ = req.body.firstName;
	var lastNameQ = req.body.lastName;
	var emailQ = req.body.email;
	var favGenreQ = req.body.favGenre;
	var favAuthorQ = req.body.favAuthor;
	var passwordQ = req.body.password;
	var insert_statement = `INSERT INTO users (firstName, lastName, membersince, Email, favGenres, favAuthors, Password) VALUES ('${firstNameQ}', '${lastNameQ}', to_date('20220412','yyyymmdd'), '${emailQ}', ARRAY ['${favGenreQ}'], ARRAY ['${favAuthorQ}'], '${passwordQ}');`
	var user_test = `SELECT * FROM users;`
	db.task('get-everything', task => {
        return task.batch([
            task.any(insert_statement),
			task.any(user_test)
        ]);
    })
    .then(info => {
		console.log('success');
    	res.render('pages/home',{
				my_title: "Home",
				local_css: "home_style.css",
				author: info[0][0].favauthors,
				genre: info[1][0].favgenres
			})
    })
    .catch(err => {
            console.log('error', err);
            res.render('pages/login', {
                my_title: 'Login',
				local_css: "signin.css"
            })
    });
});

//LOGIN
app.post('/login', async function(req, res) {
	var passwordQ = req.body.userPass;
	var emailQ = req.body.userEmail;

	var checkQ = `SELECT password FROM users WHERE Email = '${emailQ}' AND password = '${passwordQ}';`;
	var userQ = `SELECT accountid FROM users WHERE email = '${emailQ}';`
	var promise = [];
	promise.push(db.task('get-info', task =>{
		return task.batch([
			task.any(checkQ),
			task.any(userQ)
		])
	})
	.then(info =>{
		console.log(info[1][0].accountid);
		res.cookie('session_id', Math.floor((Math.random() * 10) + 1));
		res.setHeader("set-cookie",["id="+info[1][0].accountid]);
	})
	.catch(err => {
		console.log('error', err);
		res.render('pages/login', {
			my_title: 'Login',
			local_css: "signin.css"
		})
	}));
	await promise[0];
	id = req.cookies.id;
	console.log(req.cookies.id);
	var authorQ = `SELECT favAuthors FROM users WHERE accountid = '${id}';`;
	var genreQ = `SELECT favGenres FROM users WHERE accountid = '${id}';`;
	db.task('get-everything', task => {
        return task.batch([
			task.any(checkQ),
			task.any(authorQ),
			task.any(genreQ)
        ]);
    })
    .then(info => {
    	if (info[0] != '') {
			res.render('pages/home',{
				my_title: "Home",
				local_css: "home_style.css",
				author: info[1][0].favauthors,
				genre: info[2][0].favgenres
			})
		} 
    })
    .catch(err => {
            console.log('error', err);
            res.render('pages/login', {
                my_title: 'Login',
				local_css: "signin.css"
            })
    });
})

//HOME
app.get('/home', function(req, res) {
	var authorQ = `SELECT favAuthors FROM users WHERE accountid = '${id}';`; //UPDATE WHEN COOKIES WORK
	var genreQ = `SELECT favGenres FROM users WHERE accountid = '${id}';`;
	db.task('get-everything', task => {
		return task.batch([
			task.any(authorQ),
			task.any(genreQ)
		]);
	})
	.then(info => {
		console.log(info[1][0]);
		res.render('pages/home',{
			local_css:"home_style.css",
			my_title:"Home",
			author: info[0][0].favauthors,
			genre: info[1][0].favgenres
		});
	})	
});

// ACCOUNT
app.get('/profile',async function(req,res){
	promiseLst = [];
	var bookQ = `SELECT books FROM users WHERE accountid = '${id}';`; // UPDATE WHEN COOKIES WORK
	promiseLst.push(db.task('get-everything', task => {
		return task.batch([
			task.any(bookQ)
		]);
	})
	.then(info => {
		bookList = info[0][0].books
	}));
	await promiseLst[0];
	bookData = [];
	promiseLst = [];
	if(bookList != null){
		bookList.forEach(function(book) {
			promiseLst.push(axios({
				url: `https://www.googleapis.com/books/v1/volumes?q=isbn:${book}&orderBy=newest`,
					method:'GET',
					dataType: 'json'
			})
			.then(items =>{
				bookData.push(items.data.items[0].volumeInfo);
			}))
		})
		for(var i = 0; i < promiseLst.length; i++) {
			await promiseLst[i];
		}
	}
	var firstNameQ = `SELECT firstname FROM users WHERE accountid = '${id}';`;
	var lastNameQ = `SELECT lastname FROM users WHERE accountid = '${id}';`;
	var emailQ = `SELECT email FROM users WHERE accountid = '${id}';`;
	var signupQ = `SELECT membersince FROM users WHERE accountid = '${id}';`;
	db.task('get-everything', task => {
		return task.batch([
			task.any(firstNameQ),
			task.any(lastNameQ),
			task.any(emailQ),
			task.any(signupQ)
		]);
	})
	.then(info => {
		res.render('pages/author',{
			local_css:"author_style.css", //FINISH WITH DB
			my_title:"[FIX THIS LATER]",
			firstname: info[0][0].firstname,
			lastname: info[1][0].lastname,
			email: info[2][0].email,
			memberDate: info[3][0].membersince.toISOString().split('T')[0],
			error:false,
			message:"ignore",
			items: bookData
		})
	})
    .catch(err => {
            console.log('error', err);
            res.render('pages/home',{
				my_title: "Home",
				author: {},
				genre: {}
			  })
    });
});

//LIBRARY
app.get('/library', async function(req, res) {
	promiseLst = [];
	var physicalQ = `SELECT physical FROM users WHERE accountid = '${id}';`; // UPDATE WHEN COOKIES WORK
	var ebookQ = `SELECT ebook FROM users WHERE accountid = '${id}';`;
	var audioQ = `SELECT audiobook FROM users WHERE accountid = '${id}';`;
	promiseLst.push(db.task('get-everything', task => {
		return task.batch([
			task.any(physicalQ),
			task.any(ebookQ),
			task.any(audioQ)
		]);
	})
	.then(info => {
		physicalList = info[0][0].physical;
		ebookList = info[1][0].ebook;
		audioList = info[2][0].audiobook;
	}));
	await promiseLst[0];

	physicalbookData = [];
	ebookData = [];
	audiobookData = [];
	promiseLst = [];
	if(physicalList != null){
		physicalList.forEach(function(book) {	
			promiseLst.push(axios({
				url: `https://www.googleapis.com/books/v1/volumes?q=isbn:${book}`,
					method:'GET',
					dataType: 'json'
			})
			.then(items =>{
				physicalbookData.push(items.data.items[0].volumeInfo);
			}))
		})
	}
	if(ebookList != null){
		ebookList.forEach(function(book) {	
			promiseLst.push(axios({
				url: `https://www.googleapis.com/books/v1/volumes?q=isbn:${book}`,
					method:'GET',
					dataType: 'json'
			})
			.then(items =>{
				ebookData.push(items.data.items[0].volumeInfo);
			}))
		})
	}
	if(audioList != null){
		audioList.forEach(function(book) {	
			promiseLst.push(axios({
				url: `https://www.googleapis.com/books/v1/volumes?q=isbn:${book}`,
					method:'GET',
					dataType: 'json'
			})
			.then(items =>{
				// console.log(items.data.items[0].volumeInfo);
				audiobookData.push(items.data.items[0].volumeInfo);
			}))
		})
	}
	for(var i = 0; i < promiseLst.length; i++) {
		await promiseLst[i];
	}
	console.log(physicalbookData);
	res.render('pages/library',{
		local_css:"library_style.css", 
		my_title:"My Library",
		physical: physicalbookData,
		ebook: ebookData,
		audio: audiobookData
	});
});

// Add book
app.post('/library', async function(req, res) {
	promiseLst = [];
	var title = req.body.bookTitle;
	var author = req.body.bookAuthor;
	var format = req.body.bookType;

	promiseLst.push(axios({
		url: `https://www.googleapis.com/books/v1/volumes?q=intitle:${title}`,
			method:'GET',
			dataType: 'json'
	})
	.then(items =>{
		isbn = items.data.items[0].volumeInfo.industryIdentifiers[0].identifier;
		console.log(isbn);
	}))
	await promiseLst[0];
	
	var bookInsert = `INSERT INTO library (isbn, title, author) VALUES ('${isbn}','${title}','${author}');`;
	var userInsert = `UPDATE users SET ${format}=array_append(${format},'${isbn}') WHERE accountid='${id}';`; //UPDATE WITH COOKIES
	var userInsert2 = `UPDATE users SET books =array_append(books,'${isbn}') WHERE accountid='${id}';`; 

	promiseLst = [];

	db.task('get-everything', task => {
        return task.batch([
            task.any(bookInsert),
			task.any(userInsert),
			task.any(userInsert2)
        ]);
    })
	.then(info => {
		res.render('pages/home',{
			my_title: "Home",
			local_css: "home_style.css",
			author: {},
			genre: {}
		})
	});
});

app.listen(3000);
console.log('3000 is the magic port');
