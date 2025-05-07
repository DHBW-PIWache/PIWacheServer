// Modal öffnen
function openModal(modalId) {
    document.querySelector(modalId).classList.add('is-active');
}

// Modal schließen
function closeModal(modalId) {
    document.querySelector(modalId).classList.remove('is-active');
}

// Öffnen-Button-Handling
document.addEventListener('click', function(event) {
    const openModalButton = event.target.closest('[data-open]');
    if (openModalButton) {
        event.preventDefault();
        const modalId = openModalButton.getAttribute('data-open');
        openModal(modalId);
    }
});


// Schließen-Button-Handling
document.addEventListener('click', function(event) {
    const closeModalButton = event.target.closest('[data-close]');
    if (closeModalButton) {
        event.preventDefault();
        const modalId = closeModalButton.getAttribute('data-close');
        closeModal(modalId);
    }
});


// Klick auf Modal-Hintergrund schließt aktives Modal
document.addEventListener('click', function(event) {
    if (event.target.classList.contains('modal-background')) {
        closeModal('.modal.is-active');
    }
});



// Mass-Delete Funktionalität
document.addEventListener('DOMContentLoaded', function() {
    const massDeleteButton = document.getElementById('massDeleteButton');
    const confirmMassDeleteButton = document.getElementById('confirmMassDeleteButton');
    const massDeleteForm = document.getElementById('massDeleteForm');
    const massDeleteIdsInput = document.getElementById('massDeleteIds');
    const massDeleteModal = document.getElementById('modal-mass-delete');

    if (massDeleteButton && confirmMassDeleteButton && massDeleteForm && massDeleteIdsInput) {

        massDeleteButton.addEventListener('click', function() {
            

            const selectedCheckboxes = document.querySelectorAll('input[name="selectedVideos"]:checked');
            const selectedIds = Array.from(selectedCheckboxes).map(cb => cb.value);

            if (selectedIds.length === 0) {
                alert('Bitte wähle mindestens ein Video aus.');
                return;
            }

            
            massDeleteIdsInput.value = selectedIds.join(',');
           
            massDeleteModal.classList.add('is-active');
        });

        confirmMassDeleteButton.addEventListener('click', function() {
            massDeleteForm.submit();
        });

       
        massDeleteModal.addEventListener('click', function(event) {
            if (event.target.classList.contains('delete') || event.target.classList.contains('button') && event.target.innerText.trim() === 'Abbrechen') {
                massDeleteModal.classList.remove('is-active');
            }
        });
    }
});


// Filter Funktion nach Client 
function filterVideosByClient() {
    const selectedClientId = document.getElementById("clientSelect").value;
    const rows = document.querySelectorAll("#videoTable tbody tr");

    rows.forEach(row => {
        const clientId = row.getAttribute("data-client-id");
        row.style.display = (selectedClientId === "all" || clientId === selectedClientId) ? "" : "none";
    });
}
