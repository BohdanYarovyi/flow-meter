import {
    fetchCurrentAccountId,
    fetchShortFlowsByAccountId,
    fetchStatisticsForLastMonth,
    fetchStatisticsForLastWeek,
    fetchStatisticsForLastYear,
    fetchStatisticsForMonthsByFlowId,
    fetchUniqueMonthsByFlowId
} from "./api.js";
import {
    cloneFlowItemStatisticsTemplate,
    cloneFlowNotFoundLabelTemplate,
    cloneMonthItemTemplate
} from "../../pub/js/template-loader.js";
import {clearContainers, hideError, showError} from "../../pub/js/util.js";
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
            flowItem.addEventListener("click", () => setFlowSelected(flowItem, flow));

            DOM.optionPanel.flows.appendChild(clone);
        });

        const firstFlow = DOM.optionPanel.flows.querySelector(".flow");
        await setFlowSelected(firstFlow, flows[0]);
    }
}

async function setFlowSelected(flowItem, flow) {
    const allFlows = DOM.optionPanel.flows.querySelectorAll(".flow");

    allFlows.forEach(flow => flow.classList.remove("selected"));
    flowItem.classList.add("selected");
    STATE.selectedFlow = flow;
    STATE.selectedScope = null;

    await fillMonths(flow.id);
    clearSelections();
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

async function fillMonths(flowId) {
    clearContainers(DOM.optionPanel.months);
    const months = await fetchUniqueMonthsByFlowId(flowId);

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
        hideError(document.querySelector(".scope-parameters__error"));
    } catch (error) {
        showError(
            error,
            document.querySelector(".scope-parameters__error"),
            document.querySelector("#scope-parameters__error-message")
        );
    }
}

async function fetchData() {
    let data;

    switch (STATE.selectedScope) {
        case PERIODS.WEEK :
            data = await getStatForLastWeek();
            break;
        case PERIODS.MONTH :
            data = await getStatForLastMonth();
            break;
        case PERIODS.YEAR :
            data = await getStatForLastYear();
            break;
        default:
            data = await getStatForMonth();
    }

    return StatisticsData.statisticDataFromJSON(data);
}

async function getStatForLastWeek() {
    const flowId = STATE.selectedFlow.id;

    return fetchStatisticsForLastWeek(flowId);
}

async function getStatForLastMonth() {
    const flowId = STATE.selectedFlow.id;

    return fetchStatisticsForLastMonth(flowId);
}

async function getStatForLastYear() {
    const flowId = STATE.selectedFlow.id;

    return fetchStatisticsForLastYear(flowId);
}

async function getStatForMonth() {
    const flowId = STATE.selectedFlow.id;
    const year = STATE.selectedScope.year
    const month = STATE.selectedScope.month;

    return fetchStatisticsForMonthsByFlowId(flowId, year, month);
}


// graphic-drawing
function showGraphic(data) {
    const context = document
        .getElementById('graph')
        .getContext('2d');

    destroyGraphIfExists();

    const options = getGraphOptions(data);
    STATE.graph = new Chart(context, options);
}

function getGraphOptions(data) {
    const type = 'line';
    const graphData = prepareGraphData(data.labels, data.values);
    const graphOptions = prepareGraphOptions(
        data.interval,
        data.year,
        data.flowTitle,
        STATE.selectedFlow.targetPercentage
    );

    return {
        type: type,
        data: graphData,
        options: graphOptions
    };
}

function prepareGraphData(labels, values) {
    return {
        labels: labels,
        datasets: [
            {
                data: values,
                borderColor: 'rgba(75, 192, 192, 1)',
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderWidth: 3,
                pointBackgroundColor: 'black',
                pointBorderColor: 'white',
                pointRadius: 4,
                pointHoverRadius: 8,
                fill: true,
                tension: 0.3,
                hidden: false,
                cubicInterpolationMode: 'default',
                stepped: false
            }
        ]
    };
}

function prepareGraphOptions(interval, year, flowTitle, goal) {
    return {
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
                        yMin: goal,
                        yMax: goal,
                        borderColor: 'red',
                        borderWidth: 1,
                        borderDash: [10, 6], // пунктир
                        label: {
                            enabled: true,
                            content: `Goal: ${goal}%`,
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
                    mode: 'x',
                    modifierKey: 'ctrl',
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
                text: `${interval}, ${year}`,
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
                text: flowTitle,
                color: '#454545',
                font: {
                    size: 19
                }
            },
            legend: {
                display: false,
                position: 'top',
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
                    text: 'Days',
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
                    text: 'Percent (%)',
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
    };
}

function destroyGraphIfExists() {
    if (STATE.graph !== null) {
        STATE.graph.destroy();
    }
}
