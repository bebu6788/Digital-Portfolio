// Imports the server.js file to be tested.
const server = require("../src/server");
// Assertion (Test Driven Development) and Should,  Expect(Behaviour driven 
// development) library
const chai = require("chai");
// Chai HTTP provides an interface for live integration testing of the API's.
const chaiHttp = require("chai-http");
chai.should();
chai.use(chaiHttp);
const { assert, expect } = chai;

describe("Server!", () => {
  // test / endpoint
  it("Returns the default welcome message", (done) => {
    chai
      .request(server)
      .get("/")
      .end((err, res) => {
        expect(res).to.have.status(200);
        done();
      });
  });


  it("Returns the default welcome message", (done) => {
  // test /search_history endpoint
    chai
      .request(server)
      .get("/search_history")
      .end((err, res) => {
        expect(res).to.have.status(200);
        done();
      });
  });

});
