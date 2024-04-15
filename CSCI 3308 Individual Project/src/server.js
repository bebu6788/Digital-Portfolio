/***********************
  Load Components!

  Express      - A Node.js Framework
  Body-Parser  - A tool to help use parse the data in a post request
  Pg-Promise   - A database tool to help use connect to our PostgreSQL database
***********************/
var express = require('express'); //Ensure our express framework has been added
var app = express();
var bodyParser = require('body-parser'); //Ensure our body-parser tool has been added
app.use(bodyParser.json());              // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies

//Create Database Connection
var pgp = require('pg-promise')();

/**********************
  Database Connection information
  host: This defines the ip address of the server hosting our database.
		We'll be using `db` as this is the name of the postgres container in our
		docker-compose.yml file. Docker will translate this into the actual ip of the
		container for us (i.e. can't be access via the Internet).
  port: This defines what port we can expect to communicate to our database.  We'll use 5432 to talk with PostgreSQL
  database: This is the name of our specific database.  From our previous lab,
		we created the football_db database, which holds our football data tables
  user: This should be left as postgres, the default user account created when PostgreSQL was installed
  password: This the password for accessing the database. We set this in the
		docker-compose.yml for now, usually that'd be in a seperate file so you're not pushing your credentials to GitHub :).
**********************/

/** If we're running in production mode (on heroku), the we use DATABASE_URL
 * to connect to Heroku Postgres.
 */
const isProduction = process.env.NODE_ENV === 'production';

const dev_dbConfig = {
	host: 'db',
	port: 5432,
	database: 'drinks_db',
	user: 'postgres',
	password: 'pwd'
};

const dbConfig = isProduction ? process.env.DATABASE_URL : dev_dbConfig;

// Heroku Postgres patch for v10
// fixes: https://github.com/vitaly-t/pg-promise/issues/711
if (isProduction) {
  pgp.pg.defaults.ssl = {rejectUnauthorized: false};
}

const db = pgp(dbConfig);

// set the view engine to ejs
app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');
app.use(express.static(__dirname + '/'));//This line is necessary for us to use relative paths and access our resources directory



// home page
app.get('/', function(req, res) {
	res.render('pages/home',{
		local_css:"home.css",
		my_title:"Home Page"
	});
});

// search history page
app.get('/search_history', function(req, res) {

    var query1 = `SELECT * FROM drinks;`;

	db.task('get-everything', task => {
        return task.batch([
            task.any(query1)

        ]);
    })

    .then(data => {
    	res.render('pages/search_history',{
		    local_css:"search_history.css",
		    my_title: "Search History",
            drink_data: data[0]
	    })

        // console.log(data[0][0].drinkid);
        // console.log(data[0][0].drinkname);
        // console.log(data[0][0].drinkalc);
        // console.log(data[0][0].drinkcat);
        // console.log(data[0][0].drinkimg);


    })

    .catch(err => {
        console.log('error', err);
    	res.render('pages/search_history',{
		    local_css:"search_history.css",
		    my_title: "Search History"

	    })
    });
});

app.post('/search_history', function(req, res) {

    var _drinkid = req.body.id;
    var _drinkname = req.body.name;
	var _drinkalc = req.body.alc;
    var _drinkcategory = req.body.category;
	var _drinkimg = req.body.img;


    var query1 = `INSERT INTO drinks (drinkID, drinkName, drinkAlc, drinkCat, drinkImg) VALUES ('${_drinkid}', '${_drinkname}', '${_drinkalc}', '${_drinkcategory}', '${_drinkimg}');`;

	db.task('get-everything', task => {
        return task.batch([
            task.any(query1)

        ]);
    })
    .then(data => {
    	res.json({message: "hello"});
    })
    .catch(err => {
        console.log('error', err);
    	res.json({message: "error"});
    });
});

//app.listen(3000);
const server = app.listen(process.env.PORT || 3000, () => {
  console.log(`Express running → PORT ${server.address().port}`);
});