package productsums.impl.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import productsums.api.process.DataStorageProcessAPIV2;
import productsums.models.process.DataStorageProcessRequestV2;
import productsums.models.process.DataStorageProcessResponseV2;

public class DataStorageProcessImpl2 implements DataStorageProcessAPIV2{
	@Override
	public Optional<Integer> writeOutputs(DataStorageProcessRequestV2 request) {
		if (request.errorResponse().isPresent()) {
			try {
				request.os().write(request.errorResponse().get().getBytes());
			} catch (IOException e) {
				return Optional.of(1);
			}
		}
		
		if (request.errorResponse().isEmpty() && request.productSumResults().isEmpty()) {
			//Should not be possible for their to be nothing for the DSP to print to file
			return Optional.of(2);
		}
		if (request.productSumResults().isEmpty()) {
			return Optional.empty();
		}
		try {
			for (Entry<Integer, Integer> entry : request.productSumResults().get().entrySet()) {
				request.os().write(
						String.format("%d:%d;", entry.getKey(), entry.getValue())
						.getBytes());
			}
		} catch (IOException e) {
			return Optional.of(1);
		}
		return Optional.empty();
	}

	@Override
	public DataStorageProcessResponseV2 readInputs(InputStream is, String delimiters) {
		int ch = 0;
		List<Integer> args = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		try {
			while ((ch = is.read()) != -1) {
				if (delimiters.contains(""+ch)) {
					if (sb.isEmpty()) {
						return new DataStorageProcessResponseV2(Optional.empty(),Optional.of(1));
					}
					try {
						args.add(Integer.parseInt(sb.toString()));
						sb = new StringBuilder();
					} catch (NumberFormatException e) {
						return new DataStorageProcessResponseV2(Optional.empty(), Optional.of(2));
					}
				} else {
					sb.append((char)ch);
				}
			}
		} catch (IOException e) {
			return new DataStorageProcessResponseV2(Optional.empty(), Optional.of(3));
		}
		return new DataStorageProcessResponseV2(Optional.of(args), Optional.empty());
	}


}
