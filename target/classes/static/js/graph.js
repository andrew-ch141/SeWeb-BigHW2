const graphElement = document.querySelector("#rdf-graph");
const statusElement = document.querySelector("#graph-status");
const uploadForm = document.querySelector("#rdf-upload-form");
const currentButton = document.querySelector("#load-current");

let cy;

function toElements(triples) {
    const nodes = new Map();
    const edges = [];

    triples.forEach((triple, index) => {
        nodes.set(triple.subject, { data: { id: triple.subject, label: triple.subject } });
        nodes.set(triple.object, { data: { id: triple.object, label: triple.object } });
        edges.push({
            data: {
                id: `edge-${index}`,
                source: triple.subject,
                target: triple.object,
                label: triple.predicate
            }
        });
    });

    return [...nodes.values(), ...edges];
}

function renderGraph(triples) {
    statusElement.textContent = `Showing ${triples.length} RDF triples.`;
    cy = cytoscape({
        container: graphElement,
        elements: toElements(triples),
        layout: { name: "cose", animate: true },
        style: [
            {
                selector: "node",
                style: {
                    "background-color": "#3157d5",
                    "color": "#111827",
                    "label": "data(label)",
                    "text-valign": "bottom",
                    "text-wrap": "wrap"
                }
            },
            {
                selector: "edge",
                style: {
                    "curve-style": "bezier",
                    "label": "data(label)",
                    "line-color": "#9ca3af",
                    "target-arrow-color": "#9ca3af",
                    "target-arrow-shape": "triangle"
                }
            }
        ]
    });
}

async function loadCurrentGraph() {
    const response = await fetch("/graph/current");
    renderGraph(await response.json());
}

uploadForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(uploadForm);
    const response = await fetch("/graph/upload", { method: "POST", body: formData });
    renderGraph(await response.json());
});

currentButton.addEventListener("click", loadCurrentGraph);
loadCurrentGraph();
