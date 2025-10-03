
describe('Login spec', () => {
  beforeEach(() => {
  cy.window().then((win) => {
    win.sessionStorage.clear()
    win.localStorage.clear()
  })
  })
  it('Login successfull', () => {
    cy.visit('/login')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')
  });
  it('Fail login',() =>{
    cy.visit('/login') 
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"mov"}{enter}{enter}`)

    cy.url().should('not.include', '/sessions')
    cy.url().should('include', '/login')

    
  })
  it('Logout successfully', ()=>{
    cy.visit('/login')
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
    
    cy.url().should('include', '/sessions')
    cy.contains('Logout').click() 
    
    cy.url().should('not.include', '/sessions')

  })
  it('cannot submit if email is not at the format', ()=>{
    cy.visit('/login')
    cy.get('input[formControlName=email]').type("yogastudio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
    
    cy.get('[type="submit"]').should('be.disabled')
  })
});
