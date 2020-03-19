import * as mostdom from '@cycle/dom/most-typings'
import { Sources } from '@cycle/run';
import { setup } from '@cycle/most-run';
import { div, input, span, makeDOMDriver, MainDOMSource } from '@cycle/dom';
import { of, Stream, startWith } from 'most';

function Checkbox(sources: Sources) {
	const click$ = sources.DOM.select('.checkbox').events('click');
	const toggleCheck$ = click$
		.filter(ev => ev.target instanceof HTMLInputElement)
		.map(ev => ev.target.checked)

	const toggled$ = startWith(false, toggleCheck$)

	const vdom$ = toggled$.map(toggled =>
		div('.checkbox', [
			input('.checkbox', {attrs: {type: 'checkbox', checked: toggled}}),
			'Label Here'
		])
	);

	return {
		DOM: vdom$
	}
}

const {sources, sinks, run} = setup(Checkbox, { DOM: makeDOMDriver('#mount')});
const dispose = run();
