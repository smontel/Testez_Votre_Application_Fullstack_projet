
describe('session spec', () => {
    beforeEach(() => {
        cy.window().then((win) => {
            win.sessionStorage.clear()
            win.localStorage.clear()
            cy.visit('/login')
            cy.get('input[formControlName=email]').type("yoga@studio.com")
            cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
        })
    })

    it('Creating session successfully',()=>{
        cy.contains('Create').click()

        cy.url().should('include', '/sessions/create')

        cy.get('input[formControlName=name]').type("Session test 1 ")
        cy.get('input[formControlName=date]').type("2026-01-01")
        cy.get('mat-select[formControlName=teacher_id]').click()
        cy.get('mat-option').first().click()
        cy.get('textarea[formControlName=description]').type("description de test")
        cy.contains('Save').click()

        cy.url().should('not.include', '/sessions/create')
    })

    it('Modifying session successfully',()=>{
        
        cy.contains('mat-card', 'Session test 1').find('button').contains('Edit').click()
        cy.url().should('include', '/sessions/update')
        cy.get('input[formControlName=name]').clear().type("Session test 1 updated")
        cy.contains('Save').click()
        cy.contains('Session test 1 updated').should('exist')

    })

    it('Opening details page successfully',()=>{
        cy.contains('mat-card', 'Session test 1 updated').find('button').contains('Detail').click()
        cy.url().should('include', '/sessions/detail')

    })

    it('Deleting session sucessfully',()=>{
        cy.contains('mat-card', 'Session test 1 updated').find('button').contains('Detail').click()
        cy.contains('Delete').click()
        cy.url().should('not.include', '/sessions/detail')
        cy.contains('Session test 1 updated').should('not.exist')
    })

    

});
