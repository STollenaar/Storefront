import React, { useState, useEffect } from 'react'

import empty from '../assets/empty.png';
import missing from '../assets/missing.png';

export default ({ item, setCurrentItem }) => {
	const [imageError, setImageError] = useState(false);

	useEffect(() => {
		setImageError(false);
	}, [item, setImageError])

	if (item === null) {
		return (
			<div className="item">
				<img title={"Empty"} alt={"Empty"} src={empty} />
			</div>
		);
	}

	const alt = `${item.name} (${item.amount})`;

	return (
		<div
			className="item"
			onClick={() => setCurrentItem(item)}
		>
			<img
				className={imageError ? 'empty' : null}
				title={alt}
				alt={alt}
				src={imageError ? missing : `./images/${item.image}.png`}
				onError={() => {
					setImageError(true);
				}}
			/>
		</div>
	);
};
