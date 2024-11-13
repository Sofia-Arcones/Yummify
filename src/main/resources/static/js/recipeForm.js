var units = /*[[${unidades}]]*/ [];
var ingredients = /*[[${ingredientes}]]*/ [];

$(document).ready(function () {
    $(".ingredient-input").autocomplete({
        source: ingredients.map(item => item.name),
        select: function (event, ui) {
            const selectedIngredientName = ui.item.value;
            const selectedIngredient = ingredients.find(item => item.name === selectedIngredientName);

            if (selectedIngredient) {
                const unitField = $(this).closest(".form-row").find("select[name='units']");
                unitField.val(selectedIngredient.predeterminedUnit);
                toggleQuantityField(unitField);
            }
        }
    });

    $('#tags-input').on('keypress', function (event) {
        if (event.which === 13) {
            event.preventDefault();
            let tagText = $(this).val().trim();
            if (tagText) {
                addTag(tagText);
                $(this).val('');
            }
        }
    });

    const baseUnitSelect = document.querySelector("select[name='units']");
    if (baseUnitSelect) {
        toggleQuantityField(baseUnitSelect);
        baseUnitSelect.addEventListener("change", function () {
            toggleQuantityField(baseUnitSelect);
        });
    }
});

function createUnitSelect() {
    const unitSelect = document.createElement("select");
    unitSelect.setAttribute("name", "units");
    unitSelect.setAttribute("class", "form-control col-md-3 ml-2");
    unitSelect.setAttribute("required", "required");

    units.forEach(unit => {
        const option = document.createElement("option");
        option.value = unit;
        option.textContent = unit;
        unitSelect.appendChild(option);
    });

    unitSelect.addEventListener("change", function () {
        toggleQuantityField(unitSelect);
    });

    return unitSelect;
}

function addIngredient() {
    const ingredientContainer = document.getElementById("ingredient-container");
    const ingredientDiv = document.createElement("div");
    ingredientDiv.setAttribute("class", "form-row align-items-center mb-2");

    const ingredientInput = document.createElement("input");
    ingredientInput.setAttribute("name", "ingredients");
    ingredientInput.setAttribute("class", "form-control col-md-4 ingredient-input");
    ingredientInput.setAttribute("placeholder", "Ingrediente");
    ingredientInput.setAttribute("required", "required");

    const quantityInput = document.createElement("input");
    quantityInput.setAttribute("name", "quantities");
    quantityInput.setAttribute("class", "form-control col-md-3 ml-2");
    quantityInput.setAttribute("type", "number");
    quantityInput.setAttribute("step", "0.01");
    quantityInput.setAttribute("placeholder", "Cantidad");
    quantityInput.setAttribute("required", "required");

    const unitSelect = createUnitSelect();

    const removeButton = document.createElement("button");
    removeButton.setAttribute("class", "btn btn-danger col-md-2 ml-2");
    removeButton.setAttribute("type", "button");
    removeButton.textContent = "Eliminar";
    removeButton.onclick = function () {
        ingredientDiv.remove();
    };

    ingredientDiv.appendChild(ingredientInput);
    ingredientDiv.appendChild(quantityInput);
    ingredientDiv.appendChild(unitSelect);
    ingredientDiv.appendChild(removeButton);
    ingredientContainer.appendChild(ingredientDiv);

    $(ingredientInput).autocomplete({
        source: ingredients.map(item => item.name),
        select: function (event, ui) {
            const selectedIngredientName = ui.item.value;
            const selectedIngredient = ingredients.find(item => item.name === selectedIngredientName);

            if (selectedIngredient) {
                const unitField = $(this).closest(".form-row").find("select[name='units']");
                unitField.val(selectedIngredient.predeterminedUnit);
                toggleQuantityField(unitField);
            }
        }
    });
}

function addTag(tagText) {
    const tagsList = document.getElementById("tags-list");

    const tagDiv = document.createElement("div");
    tagDiv.className = "badge badge-primary m-1 p-2";
    tagDiv.textContent = tagText;

    const hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "tags";
    hiddenInput.value = tagText;

    const removeButton = document.createElement("button");
    removeButton.className = "ml-1 btn btn-sm btn-danger";
    removeButton.innerHTML = "&times;";
    removeButton.onclick = function () {
        tagsList.removeChild(tagDiv);
    };

    tagDiv.appendChild(hiddenInput);
    tagDiv.appendChild(removeButton);
    tagsList.appendChild(tagDiv);
}

function toggleQuantityField(unitSelect) {
    const quantityInput = $(unitSelect).closest(".form-row").find("input[name='quantities']");
    if (unitSelect.value === "AL_GUSTO") {
        quantityInput.prop("disabled", true).val("1");
    } else {
        quantityInput.prop("disabled", false).val("");
    }
}

function addInstruction() {
    const instructionContainer = document.getElementById("instruction-container");
    const instructionDiv = document.createElement("div");
    instructionDiv.setAttribute("class", "input-group mb-2");

    const newInstruction = document.createElement("textarea");
    newInstruction.setAttribute("name", "instructions");
    newInstruction.setAttribute("class", "form-control");
    newInstruction.setAttribute("rows", "3");
    newInstruction.setAttribute("required", "required");

    const removeButton = document.createElement("div");
    removeButton.setAttribute("class", "input-group-append");
    removeButton.innerHTML = '<button class="btn btn-danger" type="button">Eliminar</button>';
    removeButton.onclick = function () {
        instructionDiv.remove();
    };

    instructionDiv.appendChild(newInstruction);
    instructionDiv.appendChild(removeButton);
    instructionContainer.appendChild(instructionDiv);
}
