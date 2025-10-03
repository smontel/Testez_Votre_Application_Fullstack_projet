
describe('register spec', ()=>{
    beforeEach(()=>{
        cy.window().then((win) => {
            win.sessionStorage.clear()
            win.localStorage.clear()
    })
    })
    it('Creating a new account successfully',()=>{
        const randomMail = "randomtest"+Math.floor(Math.random() * 1500) +"@studio.com"

        cy.visit('/register')
        cy.get('input[formControlName=firstName]').type("testName")
        cy.get('input[formControlName=lastName]').type("testLastname")
        cy.get('input[formControlName=email]').type(randomMail)
        cy.get('input[formControlName=password]').type("test!1234")

        cy.contains('Submit').click();

        cy.url().should('include', '/login')
        cy.get('input[formControlName=email]').type(randomMail)
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        cy.url().should('include', '/sessions')
    })
})
