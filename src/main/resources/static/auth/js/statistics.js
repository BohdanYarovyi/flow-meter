import {
    fetchCurrentAccountId,
    fetchShortFlowsByAccountId,
    fetchStatisticsForMonthsByFlowId,
    fetchUniqueMonthsByFlowId
} from "./api.js";
import {
    cloneFlowItemStatisticsTemplate,
    cloneFlowNotFoundLabelTemplate,
    cloneMonthItemTemplate
} from "../../pub/js/template-loader.js";
import {clearContainers} from "../../pub/js/util.js";
import {StatisticsData} from "./classes.js";

const DOM = {
    optionPanel: {
        flows: document.querySelector("#option-flow-list"),
        months: document.querySelector("#months-list"),
        flowTabBtn: document.querySelector("#flow-tab-btn"),
        scopeTabBtn: document.querySelector("#scope-tab-btn"),
    }
};

const STATE = {
    currentAccountId: null,
    selectedFlow: null,
    selectedScope: null,
    graph: null,
};


const PERIODS = {
    WEEK: "week",
    MONTH: "month",
    YEAR: "year",
};

// init
window.onload = setupPage;

async function setupPage() {
    await initCurrentAccountId();
    await fillFlows();
    await fillMonths();
    setupTabButtons();
    initQuickSelectionButtons();
    setupButtons();
}

async function initCurrentAccountId() {
    STATE.currentAccountId = await fetchCurrentAccountId();
}

function setupButtons() {
    const generateBtn = document.querySelector("#generate-btn");

    generateBtn.addEventListener("click", () => generate());
}


// tabs
function setupTabButtons() {
    const tabButtons = document.querySelectorAll(".tab-button");
    const tabs = document.querySelectorAll(".tab");
    const tabIds = Array.from(tabs).map(tab => tab.id);

    tabButtons.forEach((button, index) => {
        button.addEventListener("click", () => {
            tabButtons.forEach(btn => btn.classList.remove("tab-button--active"));
            button.classList.add("tab-button--active");
            swapTab(tabIds[index]);
        });
    });
}

function swapTab(tabId) {
    const allTabs = document.querySelectorAll('.tab');

    allTabs.forEach(tab => {
        tab.classList.toggle("hidden", tab.id !== tabId);
    });
}


// flows tab
async function fillFlows() {
    const flows = await fetchShortFlowsByAccountId(STATE.currentAccountId);

    if (!flows || flows.length === 0) {
        const clone = cloneFlowNotFoundLabelTemplate();
        DOM.optionPanel.flows.appendChild(clone);
        disableOptionPane();
    } else {
        flows.forEach(flow => {
            const clone = cloneFlowItemStatisticsTemplate();
            const flowItem = clone.querySelector(".flow");

            flowItem.textContent = flow.title;
            flowItem.addEventListener("click", () => setFlowSelected(flowItem, flow.id));

            DOM.optionPanel.flows.appendChild(clone);
        });

        const firstFlow = DOM.optionPanel.flows.querySelector(".flow");
        await setFlowSelected(firstFlow, flows[0].id);
    }
}

async function setFlowSelected(flowItem, flowId) {
    const allFlows = DOM.optionPanel.flows.querySelectorAll(".flow");

    allFlows.forEach(flow => flow.classList.remove("selected"));
    flowItem.classList.add("selected");
    STATE.selectedFlow = flowId;
    STATE.selectedScope = null;
    await fillMonths(flowId);
}

function disableOptionPane() {
    const scopeTabBtn = DOM.optionPanel.scopeTabBtn;
    scopeTabBtn.disabled = true;
    scopeTabBtn.classList.toggle("disabled");
}


// scope tab
function initQuickSelectionButtons() {
    const quickBtns = [
        {id: "#last-week-qb", period: PERIODS.WEEK},
        {id: "#last-month-qb", period: PERIODS.MONTH},
        {id: "#last-year-qb", period: PERIODS.YEAR},
    ];

    quickBtns.forEach(btn => {
        const qButton = document.querySelector(btn.id);
        qButton.addEventListener("click", () => setQuickButtonSelected(qButton, btn.period));
    });
}

function setQuickButtonSelected(btn, period) {
    clearSelections();

    btn.classList.toggle("selected");
    STATE.selectedScope = period;
}

async function fillMonths() {
    clearContainers(DOM.optionPanel.months);
    const months = await fetchUniqueMonthsByFlowId(STATE.selectedFlow);

    months.forEach(month => {
        const clone = cloneMonthItemTemplate();

        clone.querySelector(".month-item__month").textContent = month.month;
        clone.querySelector(".month-item__year").textContent = month.year;

        const btn = clone.querySelector(".month-picker__month-item");
        btn.addEventListener("click", () => setMonthSelected(btn, month));

        DOM.optionPanel.months.appendChild(clone);
    });
}

function setMonthSelected(btn, monthInfo) {
    clearSelections();

    btn.classList.toggle("selected");
    STATE.selectedScope = monthInfo;
}

function clearSelections() {
    const allMonthBtns = document.querySelectorAll(".month-picker__month-item");
    const allQuickRangeBtns = document.querySelectorAll(".quick-range-button");

    [...allQuickRangeBtns, ...allMonthBtns].forEach(btn => btn.classList.remove("selected"));
}


// graphic
async function generate() {
    try {
        if (!STATE.selectedScope || !STATE.selectedFlow) {
            throw new Error("You need to select some option")
        }

        const data = await fetchData();
        showGraphic(data);
    } catch (error) {
        console.log("Error: ", error);
        // todo: show error here
    }
}

async function fetchData() {
    let data;

    // todo: finish fetches by different types and refactor this peace of shit
    switch (STATE.selectedScope) {
        case PERIODS.WEEK :
            console.log(PERIODS.WEEK);
            break;
        case PERIODS.MONTH :
            console.log(PERIODS.MONTH);
            break;
        case PERIODS.YEAR :
            console.log(PERIODS.YEAR);
            break;
        default:
            data = await getStatisticsByMonth(STATE.selectedScope.year, STATE.selectedScope.month);
    }
    return StatisticsData.statisticDataFromJSON(data);
}

async function getStatisticsByMonth(year, month) {
    return await fetchStatisticsForMonthsByFlowId(STATE.selectedFlow, year, month);
}

// todo: порізати конфігурацію графіка на шматки. Або функціями, або змінними
function showGraphic(data) {
    const context = document
        .getElementById('graph')
        .getContext('2d');

    destroyGraphIfExists();
    STATE.graph = new Chart(context, {
        type: 'line', // line, bar, radar, pie, doughnut, polarArea, bubble, scatter
        data: {
            labels: data.labels,
            datasets: [
                {
                    data: data.values,
                    borderColor: 'rgba(75, 192, 192, 1)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderWidth: 3,
                    pointBackgroundColor: 'black',
                    pointBorderColor: 'white',
                    pointRadius: 4,
                    pointHoverRadius: 8,
                    fill: true,
                    tension: 0.3, // 0 - лінії прямі, >0 - згладжування
                    hidden: false,
                    cubicInterpolationMode: 'default',
                    stepped: false
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false, // Якщо false — можна задавати розміри через CSS
            layout: {
                padding: {
                    top: 20,
                    right: 20,
                    bottom: 20,
                    left: 20
                }
            },
            plugins: {
                annotation: {
                    annotations: {
                        line1: {
                            type: 'line',
                            yMin: 70,
                            yMax: 70,
                            borderColor: 'red',
                            borderWidth: 1,
                            borderDash: [10, 6], // пунктир
                            label: {
                                enabled: true,
                                content: 'Ціль: 70%',
                                position: 'end',
                                backgroundColor: 'rgba(255,255,255,0.8)',
                                color: 'red',
                                font: {
                                    weight: 'bold'
                                }
                            }
                        }
                    }
                },
                zoom: {
                    pan: {
                        enabled: true,
                        mode: 'x', // 'x', 'y', або 'xy'
                        modifierKey: 'ctrl', // Наприклад, тільки з Ctrl
                    },
                    zoom: {
                        wheel: {
                            enabled: true,
                        },
                        pinch: {
                            enabled: true
                        },
                        mode: 'x',
                    }
                },
                title: {
                    display: true,
                    text: `${data.month}, ${data.year}`,
                    font: {
                        size: 22
                    },
                    padding: {
                        top: 10,
                        bottom: 30
                    },
                    color: '#333'
                },
                subtitle: {
                    display: true,
                    text: data.flowTitle,
                    color: '#454545',
                    font: {
                        size: 19
                    }
                },
                legend: {
                    display: false,
                    position: 'top', // top, left, bottom, right
                    labels: {
                        color: 'black',
                        font: {
                            size: 14
                        },
                        boxWidth: 20
                    }
                },
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(0,0,0,0.7)',
                    titleColor: '#fff',
                    bodyColor: '#fff',
                    padding: 10,
                    borderColor: '#ddd',
                    borderWidth: 1
                }
            },
            scales: {
                x: {
                    display: true,
                    title: {
                        display: true,
                        text: 'Дні',
                        color: '#333',
                        font: {
                            size: 17
                        }
                    },
                    grid: {
                        display: true,
                        color: '#e0e0e0',
                        lineWidth: 1
                    },
                    ticks: {
                        color: '#333',
                        font: {
                            size: 15
                        }
                    }
                },
                y: {
                    display: true,
                    beginAtZero: true,
                    min: 0,
                    max: 100,
                    title: {
                        display: true,
                        text: 'Відсотки (%)',
                        color: '#333',
                        font: {
                            size: 17
                        }
                    },
                    grid: {
                        display: true,
                        color: '#f0f0f0'
                    },
                    ticks: {
                        stepSize: 5,
                        color: '#333',
                        font: {
                            size: 15
                        }
                    }
                }
            },
            animation: {
                duration: 600,
                easing: 'easeInOutQuart'
            },
            hover: {
                mode: 'nearest',
                intersect: true
            },
            interaction: {
                mode: 'index',
                intersect: false
            },
            elements: {
                line: {
                    borderWidth: 3
                },
                point: {
                    radius: 5
                }
            }
        }
    });
}

function destroyGraphIfExists() {
    if (STATE.graph !== null) {
        STATE.graph.destroy();
    }
}
