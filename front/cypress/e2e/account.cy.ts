

describe('Account information spec', () => {
  beforeEach(() => {
  cy.window().then((win) => {
    win.sessionStorage.clear()
    win.localStorage.clear()
    cy.visit('/login')
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
  })
})
  it('Account information displayed correctly', () => {
    cy.contains('Account').click()
    cy.contains('Name').should('contain', 'Admin ADMIN')
    cy.contains('Email').should('contain', 'yoga@studio.com')
  });
 
});
